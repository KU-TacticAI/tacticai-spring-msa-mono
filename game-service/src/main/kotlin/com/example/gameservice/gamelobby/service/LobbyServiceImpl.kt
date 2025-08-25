package com.example.gameservice.gamelobby.service

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
import org.springframework.http.MediaType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@Service
class LobbyServiceImpl(
    private val gameRoomRepository: GameRoomRepository,
    private val gameLobbyParticipantRepository: GameLobbyParticipantRepository,
    private val webClient: WebClient,
    private val coreClient: CoreClient,
    private val messagingTemplate: SimpMessagingTemplate
) : LobbyService {

    override fun createRoom(userId: String, requestData: CreateRoomRequestDto): RoomResponseDto {
        val user = coreClient.getUserById(userId = userId.toLong());

        // maxPlayers를 DTO나 GameRoom 엔티티에 추가
        val maxPlayers = 2

        val room = GameRoom(
            status = GameRoomStatus.WAITING,
            gameType = requestData.getGameType(),
            roomName = requestData.getRoomName(),
            createdByUserId = userId.toLong(),
            hostRanking = user.ranking,
            maxPlayers = maxPlayers // GameRoom 엔티티에 필드 추가 필요
        )
        val savedRoom = gameRoomRepository.save(room)

        // 방을 만들 때 호스트가 첫 번째 참가자가 됨
        val player = gameLobbyParticipantRepository.save(GameLobbyParticipant(
            joinOrder = 1,
            joinedAt = LocalDateTime.now(),
            userId = userId.toLong(),
            roomId = savedRoom.getId(),
            isReady = false,
        ))

        val playerDto = PlayerSummaryDto(
                userId = user.userId,
                nickname = user.nickname,
                ranking = user.ranking,
                profileUrl = user.profileLink,
                ready = true,
                joinOrder = 1,
            )
        
        val roomResponse = RoomResponseDto.from(savedRoom, listOf(player), listOf(playerDto))
        broadcastRoomState(savedRoom.getId())
        return roomResponse
    }

    override fun findAllRooms(gameName: String): List<RoomResponseDto> {
        val rooms = if ("all".equals(gameName)) {
            gameRoomRepository.findAll()
        } else {
            gameRoomRepository.findAllByGameType(gameName)
        }

        val roomIds = rooms.map { it.getId() }

        val allParticipants = gameLobbyParticipantRepository.findAllByRoomIdIn(roomIds)
        val participantsMap = allParticipants.groupBy { it.roomId }

        // 5. 모든 참가자의 유저 ID를 추출합니다.
        val allUserIds = allParticipants.map { it.userId }.distinct()

        // 6. 모든 사용자 정보를 한 번에 가져옵니다. (API 호출 1)
        val userMap = coreClient.getUserByIds(allUserIds).associateBy { it.userId }

        // 7. 방과 참가자 정보를 연결하고 DTO를 생성합니다.
        return rooms.map { room ->
            val roomParticipants = participantsMap.getOrDefault(room.getId(), emptyList())

            val playerDtos = roomParticipants.map { p ->
                val user = userMap[p.userId]
                if (user != null) {
                    PlayerSummaryDto(
                        userId = user.userId,
                        nickname = user.nickname,
                        ranking = user.ranking,
                        profileUrl = user.profileLink,
                        ready = p.isReady,
                        joinOrder = p.joinOrder,
                    )
                } else {
                    PlayerSummaryDto(p.userId, "Unknown", "", 0, p.isReady, p.joinOrder)
                }
            }

            RoomResponseDto.from(room, roomParticipants, playerDtos)
        }
    }

    override fun leaveRoom(roomId: Long, userId: Long): String {
        val room = findByIdOrElseThrow(roomId)
        val participant = findByRoomIdAndUserIdOrElseThrow(roomId, userId)

        gameLobbyParticipantRepository.delete(participant)

        // 나간 사람이 방장일 경우
        if (room.getCreatedByUserId() == userId) {
//            val remainingParticipants = gameLobbyParticipantRepository.findByRoomId(roomId)
//            if (remainingParticipants.isEmpty()) {
                // 마지막 참여자였다면 방 삭제
                gameRoomRepository.delete(room)
                // TODO: 방 삭제에 대한 알림을 별도로 보낼 수 있음
                return "방이 삭제되었습니다."
//            } else {
//                // 다음으로 들어온 사람에게 방장 위임 (joinedAt 기준으로 정렬)
//                val newHost = remainingParticipants.sortedBy { it.joinedAt }.first()
//                room.createdByUserId = newHost.userId
//                gameRoomRepository.save(room)
//            }
        }

        broadcastRoomState(roomId)
        return "퇴장 완료"
    }

    override fun findRoomById(roomId: Long): RoomResponseDto {
        val room = findByIdOrElseThrow(roomId)
        val players = gameLobbyParticipantRepository.findByRoomId(room.getId())

        val playerDtos = players.map { p ->
            val user = coreClient.getUserById(p.userId) // { id, nickname, ... }
            PlayerSummaryDto(
                userId = user.userId,
                nickname = user.nickname,
                ranking = user.ranking,
                profileUrl = user.profileLink,
                ready = p.isReady,
                joinOrder = p.joinOrder,
            )
        }

        return RoomResponseDto.from(room, players, playerDtos)
    }

    override fun enterRoom(roomId: Long, userId:Long): String {
        val countParticipant = gameLobbyParticipantRepository.countByRoomId(roomId)
        val participant = GameLobbyParticipant(
            roomId = roomId,
            userId = userId,
            joinOrder = countParticipant + 1,
            joinedAt = LocalDateTime.now(),
            isReady = false,
        )
        gameLobbyParticipantRepository.save(participant)
        
        broadcastRoomState(roomId)
        return "입장 완료"
    }

    override fun selectAi(roomId: Long, userId: Long, aiId: Long): ResponseLobbyDto {
        val participant = findByRoomIdAndUserIdOrElseThrow(roomId, userId)

        participant.selectedAiId = aiId
        val updatedParticipant = gameLobbyParticipantRepository.save(participant)

        broadcastRoomState(roomId)
        return ResponseLobbyDto.from(updatedParticipant)
    }

    override fun startRoom(roomId: Long): RoomResponseDto {
        val room = findByIdOrElseThrow(roomId)

        room.status = GameRoomStatus.IN_PROGRESS
        val updatedRoom = gameRoomRepository.save(room)

        sendGameRequestToFastApi(room)

        val players = gameLobbyParticipantRepository.findByRoomId(room.getId())

        val playerDtos = players.map { p ->
            val user = coreClient.getUserById(p.userId) // { id, nickname, ... }
            PlayerSummaryDto(
                userId = user.userId,
                nickname = user.nickname,
                ranking = user.ranking,
                profileUrl = user.profileLink,
                ready = p.isReady,
                joinOrder = p.joinOrder,
            )
        }
        
        val roomResponse = RoomResponseDto.from(updatedRoom, players, playerDtos)
        broadcastRoomState(roomId)
        return roomResponse
    }

    private fun sendGameRequestToFastApi(room: GameRoom) {
        val participantList = gameLobbyParticipantRepository.findByRoomId(room.getId())
        val userIdList = participantList.map { it.userId }
        val selectedAiIds = participantList.map { it.selectedAiId }
        val aiModelInfos = coreClient.getAiUrlsByIds(selectedAiIds)

        val modelUrls = participantList.map { p ->
            val aiId = p.selectedAiId
            aiModelInfos.find { it.id == aiId }?.url ?: "http://default-model"
        }

        val body = mapOf(
            "game_id" to room.getId().toString(),
            "game_type" to room.getGameType(),
            "ai_model_ids" to selectedAiIds.map { it.toString() },
            "ai_model_urls" to modelUrls,
            "player_names" to userIdList.map { it.toString() }
        )

        webClient.post()
            .uri("/game-request")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Void::class.java)
            .doOnSuccess { println("게임 요청 전송 성공") }
            .doOnError { e -> println("게임 요청 실패: ${e.message}") }
            .subscribe()
    }

    private fun findByIdOrElseThrow(roomId: Long): GameRoom {
        return gameRoomRepository.findById(roomId).orElseThrow { NotFoundException(GameException.NOT_FOUND_AI) }
    }

    private fun findByRoomIdAndUserIdOrElseThrow(roomId: Long, userId: Long): GameLobbyParticipant {
        return gameLobbyParticipantRepository.findByRoomIdAndUserId(roomId, userId)
            .orElseThrow { NotFoundException(GameException.NOT_FOUND_AI) }
    }
    
    private fun broadcastRoomState(roomId: Long) {
        val roomState = findRoomById(roomId)
        val topic = "/topic/game.room.$roomId.state"
        messagingTemplate.convertAndSend(topic, roomState)
    }

    private fun findGameLobbyState(gameName: String) {
        val rooms = if ("all".equals(gameName)) {
            gameRoomRepository.findAll()
        } else {
            gameRoomRepository.findAllByGameType(gameName)
        }

        rooms.map { room ->
            val players = gameLobbyParticipantRepository.findByRoomId(room.getId())

            val playerDtos = players.map { p ->
                val user = coreClient.getUserById(p.userId) // { id, nickname, ... }
                PlayerSummaryDto(
                    userId = user.userId,
                    nickname = user.nickname,
                    ranking = user.ranking,
                    profileUrl = user.profileLink,
                    ready = p.isReady,
                    joinOrder = p.joinOrder,
                )
            }
            val topic = "/topic/lobby/$gameName"
            messagingTemplate.convertAndSend(topic, RoomResponseDto.from(room, players, playerDtos))
        }
    }
}
