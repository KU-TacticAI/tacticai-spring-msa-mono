package com.example.gameservice.gamelobby.dto

import com.example.gameservice.gamelobby.entity.GameRoom

data class RoomResponseDto(
    private val roomId: Long,
    private val game: String,
    private val status: String,
    private val hostUserId: Long,
){
    fun getRoomId() = roomId
    fun getGame() = game
    fun getStatus() = status
    fun getHostUserId() = hostUserId

    companion object{
        fun from(room : GameRoom): RoomResponseDto =
            RoomResponseDto(room.getId(), room.getGameType(), room.status.toString(), room.getCreatedByUserId())
    }
}
