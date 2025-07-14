package com.example.gameservice.game_session.service

import com.example.commonmodule.dto.GameResultResponseDto
import com.example.gameservice.game_session.dto.GameSessionResponseDto

interface GameSessionService {
    fun getSnapShot(roomId: Long, id: Long): GameSessionResponseDto?
    fun receiveSnapShot(roomId: Long): GameSessionResponseDto?
    fun getResult(roomId: String): GameResultResponseDto?
}