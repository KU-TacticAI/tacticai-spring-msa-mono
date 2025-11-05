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
import java.util.concurrent.ConcurrentHashMap

data class JoinRequestDto(
    val roomId: String,
    val userId: Long,
    val playerName: String?
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

    private val sessionInfoMap = ConcurrentHashMap<String, Pair<String, Long>>()

    @MessageMapping("game.room.{roomId}.join")
    fun joinRoom(
        @DestinationVariable roomId: String,
        @Payload joinRequest: JoinRequestDto,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        println("⚠⚠ joinRoom 시작~~~~~~~~~~~~~~~~~~~")
        val sessionId = headerAccessor.sessionId ?: return
        val userId = joinRequest.userId
        var updatedRoom: Any? = null

        // 1. 세션을 먼저 등록 (레이스 컨디션 방지)
        sessionInfoMap[sessionId] = Pair(roomId, userId)
        println("✅ Session registered: $sessionId -> Room: $roomId, User: $userId")

        try {
            // 2. 방 정보 확인
            val room = lobbyService.findRoomById(roomId.toLong())
            if (room == null) {
                println("⚠️ joinRoom: Room not found (ID: $roomId). (Race condition occurred, but session is registered)")
                // 방이 없어도 Disconnect 리스너가 leaveRoom을 호출할 것이므로,
                // leaveRoom이 null을 처리할 수 있어야 함. (이 부분은 아래 DisconnectListener에서 처리)
                return
            }

            // 3. 호스트가 아닌 경우에만 입장 시도
            if (room.getHostUserId() != joinRequest.userId.toLong()) {
                println("ℹ️ Entering room as guest...")
                lobbyService.enterRoom(roomId.toLong(), joinRequest.userId.toLong())
            } else {
                println("ℹ️ Joining as host.")
            }

            // 4. 성공 시, 브로커로 현재 참가자 목록 전송
            updatedRoom = lobbyService.findRoomById(roomId.toLong())

        } catch (e: IllegalStateException) { // "방 꽉 참" 예외
            println("⚠️ joinRoom 실패 (방 꽉 참): ${e.message}")

            // 👈 [필수 수정] 롤백: 방에 못 들어갔으므로 세션 등록 취소
            sessionInfoMap.remove(sessionId)
            println("❌ Session registration rolled back (Room Full): $sessionId")

        } catch (e: Exception) { // 기타 모든 예외
            println("❌ joinRoom 중 알 수 없는 오류: ${e.message}")

            // 👈 [필수 수정] 롤백: 알 수 없는 오류로 실패 시 세션 등록 취소
            sessionInfoMap.remove(sessionId)
            println("❌ Session registration rolled back (Unknown Error): $sessionId")
        }

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
        @Payload request: LeaveRequestDto,
        headerAccessor: SimpMessageHeaderAccessor
    ) {

        headerAccessor.sessionId?.let {
            sessionInfoMap.remove(it)
            println("👋 Session manually removed: $it")
        }

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

        val sessionInfo = sessionInfoMap.remove(sessionId)

        if (sessionInfo != null) {
            val (roomId, userId) = sessionInfo
            println("🚨 WebSocket disconnected: $sessionId. User $userId leaving room $roomId")

            try {
                lobbyService.leaveRoom(roomId.toLong(), userId)

                val updatedRoom = lobbyService.findRoomById(roomId.toLong())
                if (updatedRoom != null) {
                    messagingTemplate.convertAndSend("/topic/game.room.$roomId.state", updatedRoom)
                }

            } catch (e: Exception) {
                println("⚠️ Disconnect leaveRoom 오류: ${e.message}")
            }
        } else {
            // 맵에 없는 세션 (예: 방에 join하기 전 로비에서만 있다가 나간 경우)
            println("⚠️ WebSocket disconnected: $sessionId (No room/user mapping found, likely lobby user)")
        }
    }
}