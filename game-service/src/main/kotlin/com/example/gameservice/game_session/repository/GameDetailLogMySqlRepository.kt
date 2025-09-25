package com.example.gameservice.game_session.repository

import com.example.gameservice.game_session.entity.GameDetailLogMySql
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameDetailLogMySqlRepository : JpaRepository<GameDetailLogMySql, Long> {
    fun findTopByGameSessionIdOrderByIdDesc(gameSessionId: Long): GameDetailLogMySql?
}