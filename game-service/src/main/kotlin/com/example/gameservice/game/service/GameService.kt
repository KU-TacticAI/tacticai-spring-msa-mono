package com.example.gameservice.game.service

import com.example.gameservice.game.dto.CreateGameRequestDto
import com.example.gameservice.game.dto.GameResponseDto
import com.example.gameservice.game.dto.UpdateGameRequestDto

interface GameService {
    fun createGame(requestDto: CreateGameRequestDto): GameResponseDto?
    fun getGames(): List<GameResponseDto>?
    fun getGameById(id: Long): GameResponseDto?
    fun updateGame(id: Long, requestDto: UpdateGameRequestDto): GameResponseDto?
    fun deleteGame(id: Long): String?
}