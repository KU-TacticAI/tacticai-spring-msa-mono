package com.example.gameservice.game_session.entity

import jakarta.persistence.*

@Entity
@Table(name = "GameSession")
data class GameSession (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "game_id")
    val gameId: Long? = null,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "gameroom_id")
    val gameRoomId: Long? = null,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "gameresult_id")
    val gameResultId: Long? = null
)