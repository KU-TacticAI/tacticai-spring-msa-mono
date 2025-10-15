package com.example.gameservice.gamelobby.controller

import com.example.gameservice.gamelobby.service.LobbyService
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Controller
import org.springframework.web.socket.messaging.SessionDisconnectEvent

data class JoinRequestDto(
    val roomId: String,
    val userId: Long,
    val playerName: String
)

data class ReadyRequestDto(
    val userId: Long
)

data class SelectAiRequestDto(
    val userId: Long,
    val aiId: Long
)

data class LeaveRequestDto(
    val userId: Long
)

@Controller
class LobbySocketController(
    private val lobbyService: LobbyService,
    private val messagingTemplate: SimpMessagingTemplate
) {

    @MessageMapping("game.room.{roomId}.join")
    fun joinRoom(
        @DestinationVariable roomId: String,
        @Payload joinRequest: JoinRequestDto,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val sessionId = headerAccessor.sessionId ?: return

        // 1. 방 정보 확인 및 입장
        val room = lobbyService.findRoomById(roomId.toLong()) ?: return
        if (room.getHostUserId() != joinRequest.userId.toLong()) {
            lobbyService.enterRoom(roomId.toLong(), joinRequest.userId.toLong())
        }

        // 2. 브로커로 현재 참가자 목록 전송
        val updatedRoom = lobbyService.findRoomById(roomId.toLong())
        if (updatedRoom != null) {
            messagingTemplate.convertAndSend("/topic/game.room.$roomId.state", updatedRoom)
        }
    }

    @MessageMapping("game.room.{roomId}.ready")
    fun readyUp(
        @DestinationVariable roomId: String,
        @Payload readyRequest: ReadyRequestDto
    ) {
        lobbyService.updateReady(
            "ready",
            roomId.toLong(),
            readyRequest.userId.toLong()
        )
        val updatedRoom = lobbyService.findRoomById(roomId.toLong())
        if (updatedRoom != null) {
            messagingTemplate.convertAndSend("/topic/game.room.$roomId.state", updatedRoom)
        }
    }

    @MessageMapping("game.room.{roomId}.start")
    fun startGame(@DestinationVariable roomId: String) {
        lobbyService.startRoom(roomId.toLong())
        val updatedRoom = lobbyService.findRoomById(roomId.toLong())
        if (updatedRoom != null) {
            messagingTemplate.convertAndSend("/topic/game.room.$roomId.state", updatedRoom)
        }
    }

    @MessageMapping("game.room.{roomId}.selectAi")
    fun selectAi(
        @DestinationVariable roomId: String,
        @Payload request: SelectAiRequestDto
    ) {
        try {
            lobbyService.selectAi(roomId.toLong(), request.userId.toLong(), request.aiId.toLong())
        } catch (e: Exception) {
            println("⚠️ AI 선택 중 오류: ${e.message}")
        }

        val updatedRoom = lobbyService.findRoomById(roomId.toLong())
        if (updatedRoom != null) {
            messagingTemplate.convertAndSend("/topic/game.room.$roomId.state", updatedRoom)
        }
    }

    @MessageMapping("game.room.{roomId}.leave")
    fun leaveRoom(
        @DestinationVariable roomId: String,
        @Payload request: LeaveRequestDto
    ) {
        try {
            lobbyService.leaveRoom(roomId.toLong(), request.userId.toLong())
        } catch (e: Exception) {
            println("⚠️ leaveRoom 오류: ${e.message}")
        }

        val updatedRoom = lobbyService.findRoomById(roomId.toLong())
        if (updatedRoom != null) {
            messagingTemplate.convertAndSend("/topic/game.room.$roomId.state", updatedRoom)
        }
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = headerAccessor.sessionId ?: return

        // 예: 세션이 사라졌을 때 유저 퇴장 처리 (선택적)
        println("⚠️ WebSocket disconnected: $sessionId")
    }
}