package com.example.gameservice.game_result.service

import com.example.gameservice.game_result.dto.GameResultResponseDto
import org.springframework.stereotype.Service

@Service
interface GameResultService {
    fun getGameResultByUserId(userId:Long):List<GameResultResponseDto>;
}