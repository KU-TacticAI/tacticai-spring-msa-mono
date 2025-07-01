package com.example.gameservice.game_session.entity

import jakarta.persistence.*

@Entity
@Table(name = "GameDetailLog")
data class GameDetailLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "response_time_ms")
    val responseTimeMs: Int?,

    @Column(name = "board_snapshot")
    val boardSnapshot: String?,

    @Column(name = "ai_id")
    val aiId: Long?,

    @Column(name = "gamesession_id")
    val gamesessionId: Long?,

    @Column(name = "move_id")
    val moveId: Long?
)

