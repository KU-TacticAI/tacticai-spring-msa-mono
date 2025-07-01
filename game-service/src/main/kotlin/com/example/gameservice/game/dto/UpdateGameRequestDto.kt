package com.example.gameservice.game.dto

import lombok.Getter

@Getter
data class UpdateGameRequestDto (
    private val gameType : String? = null,
    private val gameVersion : String? = null,
    private val description : String? = null,
){
    fun getGameType() = gameType;
    fun getGameVersion() = gameVersion;
    fun getDescription() = description;
}