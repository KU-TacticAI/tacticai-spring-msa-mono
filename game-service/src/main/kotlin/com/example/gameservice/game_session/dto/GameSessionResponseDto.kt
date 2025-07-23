package com.example.gameservice.game_session.dto

import com.example.gameservice.game_session.entity.GameDetailLog

data class GameSessionResponseDto(
    val id: Long,
    val responseTimeMs: Int?,
    val boardSnapshot: String?,
    val aiId: Long?,
    val gameSessionId: Long?,
    val moveId: Long?
) {
    companion object {
        fun from(entity: GameDetailLog): GameSessionResponseDto {
            return GameSessionResponseDto(
                id = entity.id,
                responseTimeMs = entity.responseTimeMs,
                boardSnapshot = entity.boardSnapshot,
                aiId = entity.aiId,
                gameSessionId = entity.gameSessionId,
                moveId = entity.moveId
            )
        }
    }
}