package com.example.gameservice.gamelobby.dto

data class ReadyRequestDto(
    private val type: String,
    private val roomId:String,
    private val userId:String,
){
    fun getType(): String {
        return type
    }

    fun getRoomId(): String {
        return roomId
    }

    fun getUserId(): String {
        return userId
    }
}
