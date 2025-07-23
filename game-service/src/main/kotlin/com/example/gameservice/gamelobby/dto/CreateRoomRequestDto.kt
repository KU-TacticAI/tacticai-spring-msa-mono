package com.example.gameservice.gamelobby.dto

data class CreateRoomRequestDto(
    private val gameId: Long,
    private val gameType: String,
){
    fun getGameId() = gameId;
    fun getGameType() = gameType;
}