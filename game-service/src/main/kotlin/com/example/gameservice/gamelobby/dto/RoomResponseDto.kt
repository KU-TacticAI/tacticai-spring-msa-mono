package com.example.gameservice.gamelobby.dto

import com.example.gameservice.gamelobby.entity.GameLobbyParticipant
import com.example.gameservice.gamelobby.entity.GameRoom

data class RoomResponseDto(
    private val roomId: Long,
    private val gameType: String,
    private val roomName:String,
    private val status: String,
    private val hostUserId: Long,
    private val hostRanking: Long,
    private val playerCount: Int,
    private val maxPlayers: Int,
    private val players: List<PlayerSummaryDto>?,
){
    fun getRoomId() = roomId
    fun getGameType() = gameType
    fun getStatus() = status
    fun getHostUserId() = hostUserId
    fun getRoomName() = roomName
    fun getHostRanking() = hostRanking
    fun getPlayerCount() = playerCount
    fun getMaxPlayers() = maxPlayers
    fun getPlayers() = players

    companion object{
        fun from(
            room: GameRoom,
            playerCount: List<GameLobbyParticipant>,
            players: List<PlayerSummaryDto>?,
        ): RoomResponseDto =
            RoomResponseDto(
                roomId = room.getId(),
                gameType = room.getGameType(),
                roomName = room.getRoomName(),
                status = room.status.toString(),
                hostUserId = room.getCreatedByUserId(),
                hostRanking = room.getHostRanking(),
                playerCount = playerCount.size,
                maxPlayers = room.getMaxPlayers(),
                players = players
            );
    }
}
