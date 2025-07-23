package com.example.gameservice.game_session.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "GameResult")
data class GameResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    val roomId: Long,

    @Column(name = "createdat")
    val createdAt: LocalDateTime?,

    @Column(name = "winner_id")
    val winnerId: Long?,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "gamesession_id")
    val gameSessionId: Long? = null
)