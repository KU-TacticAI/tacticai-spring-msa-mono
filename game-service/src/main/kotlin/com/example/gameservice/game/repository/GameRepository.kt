package com.example.gameservice.game.repository

import com.example.commonmodule.exceptions.NotFoundException
import com.example.gameservice.exceptions.GameException
import com.example.gameservice.game.dto.GameResponseDto
import com.example.gameservice.game.entity.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, Long> {
    fun save(game: Game): Game;
    fun createGame(game: Game): GameResponseDto? {
        val savedGame : Game = this.save(game)
        return GameResponseDto.from(savedGame)
    }

    fun findAllGames(): List<GameResponseDto>? {
        val gameList : List<Game> = this.findAll();
        return gameList.map { g -> GameResponseDto.from(g) }
    }

    fun findGameById(id: Long): Game {
        return this.findById(id).orElseThrow { NotFoundException(GameException.NOT_FOUND_AI) }
    }
}
