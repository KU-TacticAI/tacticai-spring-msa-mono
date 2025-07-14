package com.example.gameservice.game_session.dto

data class GameRequestDto(
    val request_id: String,
    val timestamp: String,
    val game_id: String,
    val game_type: String,
    val model_ids: List<String>,
    val model_urls: List<String>,
    val players: List<String>
)
