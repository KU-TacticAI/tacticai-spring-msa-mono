package com.example.gameservice.game_session.repository

import com.example.gameservice.game_session.entity.GameDetailLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameDetailLogRepository : JpaRepository<GameDetailLog, Long> {
    fun findTopByGameSessionIdOrderByIdDesc(gameSessionId: Long): GameDetailLog?
}