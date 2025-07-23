package com.example.gameservice.gamelobby.service

import com.example.gameservice.gamelobby.dto.CreateRoomRequestDto
import com.example.gameservice.gamelobby.dto.ResponseLobbyDto
import com.example.gameservice.gamelobby.dto.RoomResponseDto

interface LobbyService {
    fun createRoom(userId: String, requestData: CreateRoomRequestDto): RoomResponseDto?
    fun findAllRooms(): List<RoomResponseDto>?
    fun enterRoom(roomId: Long, userId:Long): String?
    fun findRoomById(roomId: Long): RoomResponseDto?
    fun selectAi(roomId: Long, userId: Long, aiId: Long): ResponseLobbyDto?
    fun startRoom(roomId: Long): RoomResponseDto?
}