package com.example.gameservice.gamelobby.service

import com.example.commonmodule.dto.AiUrlsResponseDto
import com.example.gameservice.gamelobby.dto.CreateRoomRequestDto
import com.example.gameservice.gamelobby.dto.ResponseLobbyDto
import com.example.gameservice.gamelobby.dto.RoomResponseDto

interface LobbyService {
    fun createRoom(userId: String, requestData: CreateRoomRequestDto): RoomResponseDto?
    fun findAllRooms(gameName: String): List<RoomResponseDto>?
    fun enterRoom(roomId: Long, userId:Long): String?
    fun leaveRoom(roomId: Long, userId: Long): String?
    fun findRoomById(roomId: Long, selectedAi: AiUrlsResponseDto? = null): RoomResponseDto?
    fun selectAi(roomId: Long, userId: Long, aiId: Long): ResponseLobbyDto?
    fun startRoom(roomId: Long): RoomResponseDto?
    fun updateReady(type: String, roomId: Long, userId: Long)
}
