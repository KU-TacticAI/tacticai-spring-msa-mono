package com.example.gameservice.gamelobby.controller

import com.example.gameservice.game.service.GameService
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
    private val messagingTemplate: SimpMessagingTemplate,
    private val gameService: GameService
) {

    // 세션별 사용자 정보를 저장하는 맵
    private val sessionUserMap = java.util.concurrent.ConcurrentHashMap<String, Pair<Long, Long>>() // sessionId -> (roomId, userId)

    @MessageMapping("game.room.{roomId}.join")
    fun joinRoom(
        @DestinationVariable roomId: String,
        @Payload joinRequest: JoinRequestDto,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val sessionId = headerAccessor.sessionId ?: return

        // 세션 정보 저장 (연결 끊김 시 사용)
        sessionUserMap[sessionId] = Pair(roomId.toLong(), joinRequest.userId)

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

        println("✅ User ${joinRequest.userId} joined room $roomId (session: $sessionId)")
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
        @Payload request: LeaveRequestDto,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        try {
            val sessionId = headerAccessor.sessionId
            if (sessionId != null) {
                sessionUserMap.remove(sessionId)
                println("🚪 User ${request.userId} left room $roomId (session removed)")
            }

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

        println("⚠️ WebSocket disconnected: $sessionId")

        // 세션 정보가 있으면 자동으로 방 나가기 처리
        val userInfo = sessionUserMap.remove(sessionId)
        if (userInfo != null) {
            val (roomId, userId) = userInfo
            try {
                println("🔄 Auto-leaving room $roomId for user $userId due to disconnect")
                lobbyService.leaveRoom(roomId, userId)

                // 방 상태 업데이트 브로드캐스트
                val updatedRoom = lobbyService.findRoomById(roomId)
                if (updatedRoom != null) {
                    messagingTemplate.convertAndSend("/topic/game.room.$roomId.state", updatedRoom)
                }
            } catch (e: Exception) {
                println("⚠️ Auto-leave 처리 중 오류: ${e.message}")
            }
        }
    }
}