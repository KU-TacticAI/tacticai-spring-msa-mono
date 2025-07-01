package com.example.gameservice.game.entity

import com.example.commonmodule.base_entity.BaseDeletedAtEntity
import com.example.gameservice.game.dto.UpdateGameRequestDto
import jakarta.persistence.*

@Entity
@Table(name = "Game")
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @Column(name = "game_type")
    private var gameType: String?,

    @Column(name = "version")
    private var version: String?,

    @Column(name = "description")
    private var description: String?,
): BaseDeletedAtEntity() {
    fun getGameType() = gameType;
    fun getVersion() = version;
    fun getDescription() = description;

    fun updateGame(updateDto:UpdateGameRequestDto){
        updateDto.getGameType()?.let { this.gameType = it }
        updateDto.getGameVersion()?.let { this.version = it }
        updateDto.getDescription()?.let { this.description = it }
    }

    companion object {
        fun of(gameType: String?, version: String?, description: String?): Game = Game(null, gameType, version, description)
    }
}
