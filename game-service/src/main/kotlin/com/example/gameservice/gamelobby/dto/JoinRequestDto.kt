package com.example.gameservice.gamelobby.dto

data class JoinRequestDto(
    val type: String,
    val roomId: String,
    val userId: String
)
