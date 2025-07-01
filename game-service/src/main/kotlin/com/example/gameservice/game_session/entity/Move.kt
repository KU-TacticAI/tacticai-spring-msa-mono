package com.example.gameservice.game_session.entity

import jakarta.persistence.*

@Entity
@Table(name = "Move")
data class Move(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "turncount")
    val turnCount: Int?,

    @Column(name = "movedata")
    val moveData: String?,

    @Column(name = "aiid")
    val aiId: Long?,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "gamesession_id")
    val gameSessionId: Long? = null,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "aiagent_id")
    val aiAgentId: Long? = null
)
