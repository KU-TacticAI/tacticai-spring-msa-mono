package com.example.gameservice.gamelobby.dto

import com.example.commonmodule.dto.AiUrlsResponseDto
import com.example.gameservice.gamelobby.entity.GameLobbyParticipant

data class ResponseLobbyDto(
    private val userId: Long,
    private val roomId: Long,
    private val selectedAiId: Long?,
    private val name: String,
    private val description: String,
    private val gameType: String,
    private val aiUrl: String?,
){

    fun getUserId():Long{return userId}
    fun getRoomId(): Long{return roomId}
    fun getAiId(): Long?{return selectedAiId}
    fun getAiUrl():String?{return aiUrl}
    fun getName():String{return name}
    fun getDescription():String{return description}
    fun getGameType():String{return gameType}


    fun setSelectedAiId():Long?{return selectedAiId}

    companion object{
        fun from(participant : GameLobbyParticipant, selectedAi: AiUrlsResponseDto): ResponseLobbyDto =
            ResponseLobbyDto(participant.userId, participant.roomId, participant.selectedAiId
            , selectedAi.name, selectedAi.description, selectedAi.gameType, selectedAi.aiUrl)
    }
}
