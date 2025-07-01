package com.example.gameservice.gamelobby.service

import com.example.gameservice.gamelobby.dto.CreateRoomRequestDto
import com.example.gameservice.gamelobby.dto.RoomResponseDto

interface LobbyService {
    fun createRoom(requestData: CreateRoomRequestDto): RoomResponseDto?
    fun findAllRooms(): List<RoomResponseDto>?
    fun enterRoom(roomId: Long): String?
    fun findRoomById(roomId: Long): RoomResponseDto?
    fun selectAi(roomId: Long, aiId: Long): RoomResponseDto?
    fun startRoom(roomId: Long): RoomResponseDto?
    fun startGameInfo(roomId: Long): RoomResponseDto?
}