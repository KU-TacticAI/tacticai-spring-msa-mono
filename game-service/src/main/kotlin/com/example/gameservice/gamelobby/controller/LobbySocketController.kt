package com.example.gameservice.gamelobby.controller

import com.example.gameservice.gamelobby.dto.ReadyRequestDto
import com.example.gameservice.gamelobby.service.LobbyService
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class LobbySocketController(
    val lobbyService: LobbyService,
    val simpMessagingTemplate: SimpMessagingTemplate
) {

    @MessageMapping("/game.room.{roomId}.ready")
    fun readyUp(
        @DestinationVariable roomId:String,
        @Payload readyEvent: ReadyRequestDto,
    ) {
        lobbyService.updateReady(readyEvent.getType(), readyEvent.getRoomId().toLong(), readyEvent.getUserId().toLong());
    }
}