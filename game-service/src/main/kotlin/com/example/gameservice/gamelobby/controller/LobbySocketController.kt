//package com.example.gameservice.gamelobby.controller
//
//import com.example.gameservice.gamelobby.dto.JoinRequestDto
//import com.example.gameservice.gamelobby.dto.ReadyRequestDto
//import com.example.gameservice.gamelobby.dto.RoomResponseDto
//import com.example.gameservice.gamelobby.service.LobbyService
//import com.example.gameservice.redis_pubsub.service.RedisPubSubService
//import org.springframework.context.event.EventListener
//import org.springframework.http.ResponseEntity
//import org.springframework.messaging.handler.annotation.DestinationVariable
//import org.springframework.messaging.handler.annotation.MessageMapping
//import org.springframework.messaging.handler.annotation.Payload
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor
//import org.springframework.messaging.simp.SimpMessagingTemplate
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor
//import org.springframework.stereotype.Controller
//import org.springframework.web.socket.messaging.SessionDisconnectEvent
//
//@Controller
//class LobbySocketController(
//    private val lobbyService: LobbyService,
//    private val redisPubSubService: RedisPubSubService // RedisPubSubService 주입
//) {
//
//    // 클라이언트가 "/app/chat.room.{roomId}.join"으로 메시지를 보내면 이 메소드가 호출됨
//    @MessageMapping("game.room.{roomId}.join")
//    fun joinRoom(@Payload joinRequest: JoinRequestDto, headerAccessor: SimpMessageHeaderAccessor) {
//        val sessionId = headerAccessor.sessionId ?: throw IllegalStateException("Session ID cannot be null")
//        // RedisPubSubService를 통해 세션 정보 저장
//        redisPubSubService.addSession(joinRequest.roomId, joinRequest.userId, sessionId)
//        // Redis에 입장 메시지 발행
//        redisPubSubService.publish("game.room.${joinRequest.roomId}", joinRequest)
//        val response = lobbyService.findRoomById(joinRequest.roomId.toLong())
//        if (response != null) {
//            if(response.getHostUserId() != joinRequest.userId.toLong()) {
//                lobbyService.enterRoom(
//                    joinRequest.roomId.toLong(),
//                    joinRequest.userId.toLong()
//                )
//            }
//        }
//    }
//
//    // 채팅 메시지 처리
////    @MessageMapping("/chat.room.{roomId}.send")
////    fun sendChatMessage(@Payload chatMessage: ChatMessageDto) {
////        redisPubSubService.publish("chat.room.${chatMessage.roomId}", chatMessage)
////    }
//
//    // 준비 이벤트 처리
//    @MessageMapping("game.room.{roomId}.ready")
//    fun readyUp(@Payload readyEvent: Map<String, String>) {
//        redisPubSubService.publish("game.room.${readyEvent["roomId"]}", readyEvent)
//        if(readyEvent["roomId"] != null && readyEvent["userId"] != null) {
//            lobbyService.updateReady(
//                "ready",
//                readyEvent["roomId"]!!.toLong(),
//                readyEvent["userId"]!!.toLong()
//            )
//        }
//    }
//
//    // 게임 시작 이벤트 처리
//    @MessageMapping("game.room.{roomId}.start")
//    fun startGame(@Payload startEvent: Map<String, String>) {
//        redisPubSubService.publish("game.room.${startEvent["roomId"]}", startEvent)
//        lobbyService.startRoom(startEvent["roomId"]!!.toLong())
//    }
//
//    // AI 선택 이벤트 처리
//    @MessageMapping("game.room.{roomId}.selectAi")
//    fun selectAi(@Payload selectAiEvent: Map<String, Any>) {
//        redisPubSubService.publish("game.room.${selectAiEvent["roomId"]}", selectAiEvent)
//        lobbyService.selectAi(selectAiEvent["roomId"]!!.toString().toLong(), selectAiEvent["userId"]!!.toString().toLong(), selectAiEvent["aiId"].toString().toLong())
//    }
//
//    // 퇴장 이벤트 처리
//    @MessageMapping("game.room.{roomId}.leave")
//    fun leaveRoom(@Payload leaveEvent: Map<String, String>) {
//        redisPubSubService.publish("game.room.${leaveEvent["roomId"]}", leaveEvent)
//        lobbyService.leaveRoom(leaveEvent["roomId"]!!.toLong(), leaveEvent["userId"]!!.toLong())
//    }
//
//    // STOMP 레벨에서 연결이 끊어졌을 때 발생하는 이벤트를 감지
//    @EventListener
//    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
//        val headerAccessor = StompHeaderAccessor.wrap(event.message)
//        val sessionId = headerAccessor.sessionId ?: return
//
//        // 연결이 끊어진 세션을 RedisPubSubService에서 제거하고 후처리
//        redisPubSubService.removeSession(sessionId)
//    }
//}

package com.example.gameservice.gamelobby.controller

import com.example.gameservice.gamelobby.dto.*
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