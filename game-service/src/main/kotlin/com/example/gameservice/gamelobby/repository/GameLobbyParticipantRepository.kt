package com.example.gameservice.gamelobby.repository

import com.example.gameservice.gamelobby.entity.GameLobbyParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameLobbyParticipantRepository : JpaRepository<GameLobbyParticipant, Long> {
    fun countByRoomId(roomId: Long): Int

    fun findByRoomIdAndUserId(roomId: Long, userId: Long): Optional<GameLobbyParticipant>

    fun findByRoomId(roomId: Long): List<GameLobbyParticipant>

}