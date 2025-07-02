package com.example.gameservice.gamelobby.repository

import com.example.gameservice.gamelobby.entity.GameRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRoomRepository : JpaRepository<GameRoom, Long> {
}