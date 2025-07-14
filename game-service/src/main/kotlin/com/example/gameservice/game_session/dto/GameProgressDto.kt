package com.example.gameservice.game_session.dto

data class GameProgressDto(
    val request_id: String,
    val timestamp: String,
    val game_id: String,
    val game_type: String,
    val board_state: List<Int>,
    val turn_number: Int,
    val current_turn: Int,
    val players: List<String>,
    val last_move: String,
    val is_finished: Boolean,
    val winner: String?,
    val is_success: Boolean
)
