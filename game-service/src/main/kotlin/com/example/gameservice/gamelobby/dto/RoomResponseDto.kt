package com.example.gameservice.gamelobby.dto

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
}
