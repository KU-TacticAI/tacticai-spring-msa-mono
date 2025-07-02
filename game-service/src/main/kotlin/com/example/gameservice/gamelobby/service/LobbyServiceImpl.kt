package com.example.gameservice.gamelobby.service

import com.example.gameservice.gamelobby.dto.CreateRoomRequestDto
import com.example.gameservice.gamelobby.dto.RoomResponseDto
import com.example.gameservice.gamelobby.repository.GameLobbyParticipantRepository
import com.example.gameservice.gamelobby.repository.GameRoomRepository
import org.springframework.stereotype.Service

@Service
class LobbyServiceImpl(
    private val gameRoomRepository: GameRoomRepository,
    private val gameLobbyParticipantRepository: GameLobbyParticipantRepository,
) : LobbyService {
    override fun createRoom(requestData: CreateRoomRequestDto): RoomResponseDto? {
//        val room = GameRoom(
//            status = GameRoomStatus.WAITING,
//            createdByUserId = requestData.userId
//        )
//        val savedRoom = gameRoomRepository.save(room)
//        return toDto(savedRoom)
        TODO("Not yet implemented")
    }

    override fun findAllRooms(): List<RoomResponseDto>? {
        TODO("Not yet implemented")
    }

    override fun enterRoom(roomId: Long): String? {
        TODO("Not yet implemented")
    }

    override fun findRoomById(roomId: Long): RoomResponseDto? {
        TODO("Not yet implemented")
    }

    override fun selectAi(roomId: Long, aiId: Long): RoomResponseDto? {
        TODO("Not yet implemented")
    }

    override fun startRoom(roomId: Long): RoomResponseDto? {
        TODO("Not yet implemented")
    }

    override fun startGameInfo(roomId: Long): RoomResponseDto? {
        TODO("Not yet implemented")
    }
}