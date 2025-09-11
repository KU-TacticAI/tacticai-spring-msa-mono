package com.example.gameservice.gamelobby.service

import com.example.commonmodule.dto.AiUrlsResponseDto
import com.example.commonmodule.exceptions.NotFoundException
import com.example.gameservice.client.CoreClient
import com.example.gameservice.common.GameRoomStatus
import com.example.gameservice.exceptions.GameException
import com.example.gameservice.gamelobby.dto.CreateRoomRequestDto
import com.example.gameservice.gamelobby.dto.PlayerSummaryDto
import com.example.gameservice.gamelobby.dto.ResponseLobbyDto
import com.example.gameservice.gamelobby.dto.RoomResponseDto
import com.example.gameservice.gamelobby.entity.GameLobbyParticipant
import com.example.gameservice.gamelobby.entity.GameRoom
import com.example.gameservice.gamelobby.repository.GameLobbyParticipantRepository
import com.example.gameservice.gamelobby.repository.GameRoomRepository
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import reactor.core.publisher.Mono

@Service
class LobbyServiceImpl(
    private val gameRoomRepository: GameRoomRepository,
    private val gameLobbyParticipantRepository: GameLobbyParticipantRepository,
    private val webClient: WebClient,
    private val coreClient: CoreClient,
    private val messagingTemplate: SimpMessagingTemplate
) : LobbyService {

    override fun createRoom(userId: String, requestData: CreateRoomRequestDto): RoomResponseDto {
        val user = coreClient.getUserById(userId = userId.toLong())

        val maxPlayers = 2 // Or from requestData

        val room = GameRoom(
            status = GameRoomStatus.WAITING,
            gameType = requestData.getGameType(),
            roomName = requestData.getRoomName(),
            createdByUserId = userId.toLong(),
            hostRanking = user.ranking,
            maxPlayers = maxPlayers
        )
        val savedRoom = gameRoomRepository.save(room)

        gameLobbyParticipantRepository.save(GameLobbyParticipant(
            joinOrder = 1,
            joinedAt = LocalDateTime.now(),
            userId = userId.toLong(),
            roomId = savedRoom.getId(),
            isReady = false,
        ))

        // Broadcast updates to lobby subscribers
        broadcastLobbyUpdates(savedRoom.getGameType())

        // Return the state of the new room to the creator
        return findRoomById(savedRoom.getId())
    }

    override fun findAllRooms(gameName: String): List<RoomResponseDto> {
        return getLobbyState(gameName)
    }

    override fun leaveRoom(roomId: Long, userId: Long): String {
        val room = findByIdOrElseThrow(roomId)
        val gameType = room.getGameType()
        val participant = findByRoomIdAndUserIdOrElseThrow(roomId, userId)

        gameLobbyParticipantRepository.delete(participant)

        var isRoomDeleted = false
        if (room.getCreatedByUserId() == userId) {
//            val remainingParticipants = gameLobbyParticipantRepository.findByRoomId(roomId)
//            if (remainingParticipants.isEmpty()) {
                gameRoomRepository.delete(room)
                isRoomDeleted = true
//            } else {
//                val newHost = remainingParticipants.sortedBy { it.joinedAt }.first()
//                room.createdByUserId = newHost.userId
//                gameRoomRepository.save(room)
//            }
        }

        if (!isRoomDeleted) {
            broadcastRoomState(roomId) // Update room state for remaining players
        }
        broadcastLobbyUpdates(gameType) // Update lobby for everyone

        return if (isRoomDeleted) "방이 삭제되었습니다." else "퇴장 완료"
    }

    override fun findRoomById(roomId: Long, selectedAi: AiUrlsResponseDto?): RoomResponseDto {
        val room = findByIdOrElseThrow(roomId)
        val players = gameLobbyParticipantRepository.findByRoomId(room.getId())

        val allSelectedAiIds = players.mapNotNull { it.selectedAiId }.distinct()

        val aiMap = if (allSelectedAiIds.isNotEmpty()) {
            coreClient.getAiUrlsByIds(allSelectedAiIds).associateBy { it.aiId }
        } else {
            emptyMap()
        }

        val allUserIds = players.map { it.userId }.distinct()

        val userMap = if (allUserIds.isNotEmpty()) {
            coreClient.getUserByIds(allUserIds).associateBy { it.userId }
        } else {
            emptyMap()
        }

        val playerDtos = players.mapNotNull { p ->
            // userMap에서 유저 정보 가져오기
            val user = userMap[p.userId]
            if (user == null) {
                null // 유저 정보가 없으면 dto 생성 건너뛰기
            } else {
                // aiMap에서 AI 정보 가져오기
                val aiAgent = p.selectedAiId?.let { aiMap[it] }

                PlayerSummaryDto(
                    userId = user.userId,
                    nickname = user.nickname,
                    ranking = user.ranking,
                    profileUrl = user.profileLink,
                    isReady = p.isReady,
                    joinOrder = p.joinOrder,
                    selectedAi = aiAgent, // AI 정보가 있다면 그대로 할당
                )
            }
        }

        return RoomResponseDto.from(room, players, playerDtos)
    }

    override fun enterRoom(roomId: Long, userId: Long): String {
        val room = findByIdOrElseThrow(roomId)
        val countParticipant = gameLobbyParticipantRepository.countByRoomId(roomId)
        if (countParticipant >= room.getMaxPlayers()) {
            throw IllegalStateException("방이 꽉 찼습니다.")
        }

        val participant = GameLobbyParticipant(
            roomId = roomId,
            userId = userId,
            joinOrder = countParticipant + 1,
            joinedAt = LocalDateTime.now(),
            isReady = false,
        )
        gameLobbyParticipantRepository.save(participant)

        broadcastRoomState(roomId)
        broadcastLobbyUpdates(room.getGameType())
        return "입장 완료"
    }

    override fun selectAi(roomId: Long, userId: Long, aiId: Long): ResponseLobbyDto {
        val participant = findByRoomIdAndUserIdOrElseThrow(roomId, userId)
        participant.selectedAiId = aiId
        val updatedParticipant = gameLobbyParticipantRepository.save(participant)

        val selectedAi = coreClient.getAiUrlById(aiId)

        broadcastRoomState(roomId, selectedAi)
        return ResponseLobbyDto.from(updatedParticipant, selectedAi)
    }

    override fun startRoom(roomId: Long): RoomResponseDto {
        val room = findByIdOrElseThrow(roomId)
        room.status = GameRoomStatus.IN_PROGRESS
        gameRoomRepository.save(room)

        sendGameRequestToFastApi(room)

        val roomResponse = findRoomById(roomId)
        broadcastRoomState(roomId)
        broadcastLobbyUpdates(room.getGameType())
        return roomResponse
    }

    override fun updateReady(type: String, roomId: Long, userId: Long) {
        val participant = gameLobbyParticipantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow { NotFoundException(GameException.PARTICIPANT_NOT_FOUND) }

        participant.isReady = !participant.isReady
        gameLobbyParticipantRepository.save(participant)

        broadcastRoomState(roomId)
    }

    private fun sendGameRequestToFastApi(room: GameRoom) {
        // 참가자 및 선택된 AI 정보를 수집
        val participants = gameLobbyParticipantRepository.findByRoomId(room.getId())
        val playerIds = participants.map { it.userId.toString() }

        val selectedAiIds = participants.mapNotNull { it.selectedAiId }.distinct()

        // AI 정보 조회 및 모델 id/url 정렬
        val aiDtos = if (selectedAiIds.isNotEmpty()) coreClient.getAiUrlsByIds(selectedAiIds) else emptyList()
        val aiMap = aiDtos.associateBy { it.aiId }

        val models = selectedAiIds.mapNotNull { id ->
            aiMap[id]?.aiUrl?.let { url -> Pair(id.toString(), url) }
        }

        val modelIds = models.map { it.first }
        val modelUrls = models.map { it.second }

        // FastAPI의 요청 형식과 일치시키기: ai_model_ids, ai_model_urls, player_ids
        val body = mapOf(
            "request_id" to java.util.UUID.randomUUID().toString(),
            "timestamp" to java.time.Instant.now().toString(),
            "game_id" to room.getId().toString(),
            "game_type" to room.getGameType(),
            "ai_model_ids" to modelIds,
            "ai_model_urls" to modelUrls,
            "player_ids" to playerIds
        )

        // FastAPI로 비동기 전송 (실패 시 무시)
        webClient.post()
            .uri("/game-request")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Void::class.java)
            .onErrorResume { Mono.empty() }
            .subscribe()
    }

    private fun findByIdOrElseThrow(roomId: Long): GameRoom {
        return gameRoomRepository.findById(roomId).orElseThrow { NotFoundException(GameException.NOT_FOUND_AI) }
    }

    private fun findByRoomIdAndUserIdOrElseThrow(roomId: Long, userId: Long): GameLobbyParticipant {
        return gameLobbyParticipantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow { NotFoundException(GameException.NOT_FOUND_AI) }
    }

    private fun broadcastRoomState(roomId: Long, selectedAi: AiUrlsResponseDto? = null) {
        val roomState = findRoomById(roomId, selectedAi)
        val topic = "/topic/game.room.$roomId.state"
        messagingTemplate.convertAndSend(topic, roomState)
    }

    private fun broadcastLobbyUpdates(gameType: String) {
        // Send game-specific lobby update
        val gameLobbyState = getLobbyState(gameType)
        messagingTemplate.convertAndSend("/topic/lobby/$gameType", gameLobbyState)

        // Send all-games lobby update
        val allLobbyState = getLobbyState("all")
        messagingTemplate.convertAndSend("/topic/lobby/all", allLobbyState)
    }

    private fun getLobbyState(gameName: String): List<RoomResponseDto> {
        val rooms = if ("all".equals(gameName, ignoreCase = true)) {
            gameRoomRepository.findAll()
        } else {
            gameRoomRepository.findAllByGameType(gameName)
        }

        val roomIds = rooms.map { it.getId() }
        if (roomIds.isEmpty()) return emptyList()

        val allParticipants = gameLobbyParticipantRepository.findAllByRoomIdIn(roomIds)
        val participantsMap = allParticipants.groupBy { it.roomId }

        val allUserIds = allParticipants.map { it.userId }.distinct()
        val userMap = if (allUserIds.isNotEmpty()) {
            coreClient.getUserByIds(allUserIds).associateBy { it.userId }
        } else {
            emptyMap()
        }

        return rooms.map { room ->
            val roomParticipants = participantsMap.getOrDefault(room.getId(), emptyList())
            val playerDtos = roomParticipants.mapNotNull { p ->
                userMap[p.userId]?.let {
                    PlayerSummaryDto(
                        userId = it.userId,
                        nickname = it.nickname,
                        ranking = it.ranking,
                        profileUrl = it.profileLink,
                        isReady = p.isReady,
                        joinOrder = p.joinOrder,
                        selectedAi = null,
                    )
                }
            }
            RoomResponseDto.from(room, roomParticipants, playerDtos)
        }
    }
}
