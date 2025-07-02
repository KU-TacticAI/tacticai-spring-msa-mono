package com.example.gameservice.game.service

import com.example.gameservice.game.dto.CreateGameRequestDto
import com.example.gameservice.game.dto.GameResponseDto
import com.example.gameservice.game.dto.UpdateGameRequestDto
import com.example.gameservice.game.entity.Game
import com.example.gameservice.game.repository.GameRepository
import org.springframework.stereotype.Service

@Service
class GameServiceImpl(
    private val gameRepository: GameRepository
) : GameService {

    override fun createGame(requestDto: CreateGameRequestDto): GameResponseDto? {
        return gameRepository.createGame(
            Game.of(
                requestDto.getGameType(),
                requestDto.getGameVersion(),
                requestDto.getDescription()
            )
        )
    }

    override fun getGames(): List<GameResponseDto>? {
        return gameRepository.findAllGames();
    }

    override fun getGameById(id: Long): GameResponseDto? {
        return GameResponseDto.from(gameRepository.findGameById(id));
    }

    override fun updateGame(id: Long, requestDto: UpdateGameRequestDto): GameResponseDto? {
        val game:Game = gameRepository.findGameById(id);
        game.updateGame(requestDto)
        gameRepository.save(game)
        return GameResponseDto.from(game)
    }

    override fun deleteGame(id: Long): String? {
        val game:Game = gameRepository.findGameById(id);
        game.delete()
        gameRepository.save(game)
        return "삭제되었습니다."
    }
}