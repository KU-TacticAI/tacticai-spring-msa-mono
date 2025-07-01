package com.example.gameservice.gamelobby.service

import com.example.gameservice.gamelobby.dto.CreateRoomRequestDto
import com.example.gameservice.gamelobby.dto.RoomResponseDto
import org.springframework.stereotype.Service

@Service
class LobbyServiceImpl(

) : LobbyService {
    override fun createRoom(requestData: CreateRoomRequestDto): RoomResponseDto? {
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