package com.example.gameservice.game.controller

import com.example.gameservice.game.dto.CreateGameRequestDto
import com.example.gameservice.game.dto.GameResponseDto
import com.example.gameservice.game.dto.UpdateGameRequestDto
import com.example.gameservice.game.service.GameService
import org.jetbrains.annotations.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameController(
    private val gameService: GameService
) {

    @PostMapping
    fun createGame(@RequestBody requestDto: CreateGameRequestDto): ResponseEntity<GameResponseDto> {
        return ResponseEntity.ok().body(gameService.createGame(requestDto));
    }

    @GetMapping
    fun getGames(): ResponseEntity<List<GameResponseDto>> {
        return ResponseEntity.ok().body(gameService.getGames());
    }

    @GetMapping("/{id}")
    fun getGame(@PathVariable @NotNull id: Long): ResponseEntity<GameResponseDto> {
        return ResponseEntity.ok().body(gameService.getGameById(id));
    }

    @PutMapping("/{id}")
    fun updateGame(
        @PathVariable @NotNull id: Long,
        @RequestBody requestDto: UpdateGameRequestDto) : ResponseEntity<GameResponseDto> {
        return ResponseEntity.ok().body(gameService.updateGame(id, requestDto));
    }

    @DeleteMapping("/{id}")
    fun deleteGame(@PathVariable @NotNull id: Long) : ResponseEntity<String> {
        return ResponseEntity.ok().body(gameService.deleteGame(id))
    }
}