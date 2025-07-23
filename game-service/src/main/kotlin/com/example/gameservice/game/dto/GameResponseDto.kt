package com.example.gameservice.game.dto

import com.example.gameservice.game.entity.Game
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor

@Getter
@AllArgsConstructor
@NoArgsConstructor
data class GameResponseDto (
    val gameType: String?,
    val version: String?,
    val description: String?,
){
    companion object{
        fun from(game : Game): GameResponseDto =
            GameResponseDto(game.getGameType(), game.getVersion(), game.getDescription())
    }
}