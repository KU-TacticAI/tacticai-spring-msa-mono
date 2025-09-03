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

    private val roomName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status") var status: GameRoomStatus?,

    @Column(name = "created_by_user_id")
    private var createdByUserId: Long,

    private val hostRanking: Long,

    private val maxPlayers: Int,

    ){
    fun getId() : Long{return id}
    fun getGameType(): String {return gameType}
//    fun getStatus(): GameRoomStatus? {return status}
    fun getCreatedByUserId() : Long{return createdByUserId}
    fun getRoomName(): String {return roomName}
    fun getHostRanking(): Long{return hostRanking}
    fun getMaxPlayers(): Int {return maxPlayers}

    fun setUserId(id: Long):Long{
        createdByUserId = id
        return createdByUserId
    }
}
