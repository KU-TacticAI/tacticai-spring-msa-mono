package com.example.gameservice.gamelobby.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "GameLobbyParticipant")
data class GameLobbyParticipant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "join_order")
    val joinOrder: Int,

    @Column(name = "joined_at")
    val joinedAt: LocalDateTime,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "room_id")
    val roomId: Long,

    @Column(name = "selected_ai_id")
    var selectedAiId: Long? = null
)