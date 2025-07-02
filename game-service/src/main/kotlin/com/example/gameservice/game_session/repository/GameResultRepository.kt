package com.example.gameservice.game_session.repository

import com.example.gameservice.game_session.entity.GameResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameResultRepository : JpaRepository<GameResult, Long> {
}