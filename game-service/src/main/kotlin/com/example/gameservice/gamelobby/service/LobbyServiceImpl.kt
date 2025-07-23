package com.example.gameservice.gamelobby.service

import com.example.commonmodule.exceptions.NotFoundException
import com.example.gameservice.client.CoreClient
import com.example.gameservice.common.GameRoomStatus
import com.example.gameservice.exceptions.GameException
import com.example.gameservice.gamelobby.dto.CreateRoomRequestDto
import com.example.gameservice.gamelobby.dto.ResponseLobbyDto
import com.example.gameservice.gamelobby.dto.RoomResponseDto
import com.example.gameservice.gamelobby.entity.GameLobbyParticipant
import com.example.gameservice.gamelobby.entity.GameRoom
import com.example.gameservice.gamelobby.repository.GameLobbyParticipantRepository
import com.example.gameservice.gamelobby.repository.GameRoomRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Service
class LobbyServiceImpl(
    private val gameRoomRepository: GameRoomRepository,
    private val gameLobbyParticipantRepository: GameLobbyParticipantRepository,
    private val webClient: WebClient,
    private val coreClient: CoreClient,
) : LobbyService {

    override fun createRoom(userId: String, requestData: CreateRoomRequestDto): RoomResponseDto {
        val room = GameRoom(
            status = GameRoomStatus.WAITING,
            gameType = requestData.getGameType(),
            createdByUserId = userId.toLong()
        )
        val savedRoom = gameRoomRepository.save(room)

        gameLobbyParticipantRepository.save(GameLobbyParticipant(
            joinOrder = 1,
            joinedAt = LocalDateTime.now(),
            userId = userId.toLong(),
            roomId = savedRoom.getId()
        ))
        return RoomResponseDto.from(savedRoom)
    }

    override fun findAllRooms(): List<RoomResponseDto> {
        return gameRoomRepository.findAll()
            .map { RoomResponseDto.from(it) }
    }

    override fun findRoomById(roomId: Long): RoomResponseDto {
        return RoomResponseDto.from(findByIdOrElseThrow(roomId))
    }

    override fun enterRoom(roomId: Long, userId:Long): String {
        val countParticipant = gameLobbyParticipantRepository.countByRoomId(roomId)
        val participant = GameLobbyParticipant(
            roomId = roomId,
            userId = userId,
            joinOrder = countParticipant + 1,
            joinedAt = LocalDateTime.now()
        )
        gameLobbyParticipantRepository.save(participant)

        return "입장 완료"
    }

    override fun selectAi(roomId: Long, userId: Long, aiId: Long): ResponseLobbyDto {
        val participant = findByRoomIdAndUserIdOrElseThrow(roomId, userId)

        participant.selectedAiId = aiId
        val updatedParticipant = gameLobbyParticipantRepository.save(participant)

        return ResponseLobbyDto.from(updatedParticipant)
    }

    override fun startRoom(roomId: Long): RoomResponseDto {
        val room = findByIdOrElseThrow(roomId)

        room.status = GameRoomStatus.IN_PROGRESS
        val updatedRoom = gameRoomRepository.save(room)

        sendGameRequestToFastApi(room)

        return RoomResponseDto.from(updatedRoom)
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
            "request_id" to UUID.randomUUID().toString(),
            "timestamp" to Instant.now().toString(),
            "game_id" to room.getId(),
            "game_type" to room.getGameType(),
            "model_ids" to selectedAiIds,
            "model_urls" to modelUrls,
            "players" to userIdList
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
}
