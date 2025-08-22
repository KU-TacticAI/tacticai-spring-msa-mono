package com.example.gameservice.gamelobby.dto

data class CreateRoomRequestDto(
    private val roomName: String,
    private val gameType: String,
){
    fun getRoomName() = roomName;
    fun getGameType() = gameType;
}