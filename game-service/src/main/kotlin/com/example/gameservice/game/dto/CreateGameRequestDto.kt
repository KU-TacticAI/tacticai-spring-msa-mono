package com.example.gameservice.game.dto

import lombok.Getter

@Getter
data class CreateGameRequestDto (
    private val gameType : String,
    private val gameVersion : String,
    private val description : String,
){
    fun getGameType() = gameType;
    fun getGameVersion() = gameVersion;
    fun getDescription() = description;
}