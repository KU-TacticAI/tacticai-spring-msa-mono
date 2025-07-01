package com.example.gameservice.game_session.entity

import jakarta.persistence.*

@Entity
@Table(name = "GamePlayAI")
data class GamePlayAI(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "role")
    val role: String?,

    @Column(name = "joinorder")
    val joinOrder: Int?,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "gamesession_id")
    val gameSessionId: Long? = null,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "aiagent_id")
    val aiAgentId: Long? = null
)