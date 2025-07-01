package com.example.gameservice.gamelobby.entity

import jakarta.persistence.*

@Entity
@Table(name = "GameRoom")
data class GameRoom(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long,

//    @Enumerated(EnumType.STRING)
//    @Column(name = "status")
//    private val status: GameRoomStatus?,

    @Column(name = "created_by_user_id")
    private val createdByUserId: Long?,
)
