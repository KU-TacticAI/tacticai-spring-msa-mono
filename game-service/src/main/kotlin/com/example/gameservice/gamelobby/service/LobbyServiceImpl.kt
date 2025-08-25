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

        val participant = gameLobbyParticipantRepository.save(GameLobbyParticipant(
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

        val playerDtos = players.map { p ->
            val user = coreClient.getUserById(p.userId)
            PlayerSummaryDto(
                userId = user.userId,
                nickname = user.nickname,
                ranking = user.ranking,
                profileUrl = user.profileLink,
                ready = p.isReady,
                joinOrder = p.joinOrder,
                aiAgent = selectedAi,
            )
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

        val selectedAi = coreClient.getAiUrlById(aiId);

        broadcastRoomState(roomId, selectedAi)
        // Note: Selecting an AI doesn't change the lobby list, so no lobby update.
        return ResponseLobbyDto.from(updatedParticipant, selectedAi)
    }

    override fun startRoom(roomId: Long): RoomResponseDto {
        val room = findByIdOrElseThrow(roomId)
        room.status = GameRoomStatus.IN_PROGRESS
        val updatedRoom = gameRoomRepository.save(room)

        sendGameRequestToFastApi(room)

        val roomResponse = findRoomById(roomId)
        broadcastRoomState(roomId)
        broadcastLobbyUpdates(room.getGameType())
        return roomResponse
    }

    private fun sendGameRequestToFastApi(room: GameRoom) {
        // ... (omitted for brevity, no changes)
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
                        ready = p.isReady,
                        joinOrder = p.joinOrder,
                        aiAgent = null,
                    )
                }
            }
            RoomResponseDto.from(room, roomParticipants, playerDtos)
        }
    }
}
