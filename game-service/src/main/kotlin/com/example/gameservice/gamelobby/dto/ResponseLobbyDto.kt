package com.example.gameservice.gamelobby.dto

import com.example.gameservice.gamelobby.entity.GameLobbyParticipant

data class ResponseLobbyDto(
    private val userId: Long,
    private val roomId: Long,
    private val selectedAiId: Long?
){

    fun getUserId():Long{return userId}
    fun getRoomId(): Long{return roomId}
    fun setSelectedAiId():Long?{return selectedAiId}

    companion object{
        fun from(participant : GameLobbyParticipant): ResponseLobbyDto =
            ResponseLobbyDto(participant.userId, participant.roomId, participant.selectedAiId)
    }
}
