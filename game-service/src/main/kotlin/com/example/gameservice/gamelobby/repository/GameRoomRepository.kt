package com.example.gameservice.gamelobby.repository

import com.example.gameservice.gamelobby.entity.GameRoom
import org.springframework.data.jpa.repository.JpaRepository

interface GameRoomRepository : JpaRepository<GameRoom, Long> {
}
