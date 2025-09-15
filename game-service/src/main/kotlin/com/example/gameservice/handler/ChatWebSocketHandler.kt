//package com.example.gameservice.handler
//
//import com.example.gameservice.redis_pubsub.service.RedisPubSubService // 올바른 패키지 경로
//import com.fasterxml.jackson.databind.ObjectMapper
//import org.springframework.stereotype.Component
//import org.springframework.web.socket.TextMessage
//import org.springframework.web.socket.WebSocketSession
//import org.springframework.web.socket.handler.TextWebSocketHandler
//
//@Component
//class ChatWebSocketHandler(
//    private val objectMapper: ObjectMapper,
//    private val redisPubSubService: RedisPubSubService // 이제 순환 참조 없음
//) : TextWebSocketHandler() {
//
//    override fun afterConnectionEstablished(session: WebSocketSession) {
//        println("WebSocket connected: ${session.id}")
//    }
//
//    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
//        try {
//            val payload = objectMapper.readTree(message.payload)
//            val eventType = payload["type"].asText()
//            val roomId = payload["roomId"].asText()
//
//            when (eventType) {
//                "joinRoom" -> {
//                    val userId = payload["userId"].asText()
//                    // 세션 관리를 RedisPubSubService에 위임
//                    redisPubSubService.addSession(roomId, userId, session)
//                    // (선택 사항) Redis에 메시지 발행
//                    redisPubSubService.publish("chat.room.$roomId", message.payload)
//                    println("User joined room $roomId")
//                }
//                "chatMessage" -> {
//                    // 채팅 메시지를 Redis 채널에 발행
//                    redisPubSubService.publish("chat.room.$roomId", message.payload)
//                }
//                "ready", "startGame" -> {
//                    // 게임 이벤트도 Redis에 발행
//                    redisPubSubService.publish("game.room.$roomId", message.payload)
//                }
//            }
//        } catch (e: Exception) {
//            println("Error handling message: ${e.message}")
//        }
//    }
//
//    override fun afterConnectionClosed(session: WebSocketSession, status: org.springframework.web.socket.CloseStatus) {
//        println("WebSocket disconnected: ${session.id}")
//        // 세션 관리를 RedisPubSubService에 위임
//        redisPubSubService.removeSession(session)
//    }
//}