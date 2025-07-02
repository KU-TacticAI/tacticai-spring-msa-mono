package com.example.gameservice.gamelobby.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "GameLobbyParticipant")
data class GameLobbyParticipant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "join_order")
    val joinOrder: Int?,

    @Column(name = "joined_at")
    val joinedAt: LocalDateTime?,

    @Column(name = "ai_id")
    val aiId: Long?,

    @Column(name = "room_id")
    val roomId: Long?
)