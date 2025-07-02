package com.example.gameservice.game_session.repository

import com.example.gameservice.game_session.entity.GameSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameSessionRepository : JpaRepository<GameSession, Long>{
}