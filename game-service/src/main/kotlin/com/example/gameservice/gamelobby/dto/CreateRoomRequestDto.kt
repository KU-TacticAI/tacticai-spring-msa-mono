package com.example.gameservice.gamelobby.dto

data class CreateRoomRequestDto(
    private val gameId: Long,
){
    fun getGameId() = gameId;
}