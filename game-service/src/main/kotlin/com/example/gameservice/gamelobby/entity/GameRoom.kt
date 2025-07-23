package com.example.gameservice.gamelobby.entity

import com.example.gameservice.common.GameRoomStatus
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor

@Entity
@Builder
@Table(name = "game-room")
@AllArgsConstructor
@NoArgsConstructor
data class GameRoom(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long = 0,

    private val gameType: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status") var status: GameRoomStatus?,

    @Column(name = "created_by_user_id")
    private val createdByUserId: Long,
){
    fun getId() : Long{return id}
    fun getGameType(): String {return gameType}
//    fun getStatus(): GameRoomStatus? {return status}
    fun getCreatedByUserId() : Long{return createdByUserId}
}
