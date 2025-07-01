package com.example.gameservice.game_session.service

import com.example.gameservice.game_session.dto.GameSessionResponseDto
import com.example.gameservice.game_session.repository.GameSessionRepository
import org.springframework.stereotype.Service

@Service
class GameSessionServiceImpl(
    private val gameSessionRepository: GameSessionRepository
): GameSessionService {
    override fun getSnapShot(roomId: Long, id: Long): GameSessionResponseDto? {
        TODO("Not yet implemented")
    }

    override fun receiveSnapShot(roomId: Long): GameSessionResponseDto? {
        TODO("Not yet implemented")
    }

    override fun getResult(roomId: String): GameSessionResponseDto? {
        TODO("Not yet implemented")
    }
}