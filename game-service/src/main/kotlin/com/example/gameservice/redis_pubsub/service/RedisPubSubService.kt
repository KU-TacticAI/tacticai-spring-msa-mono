//package com.example.gameservice.redis_pubsub.service
//
//import com.example.gameservice.gamelobby.service.LobbyService
//import com.fasterxml.jackson.databind.ObjectMapper
//import jakarta.annotation.PostConstruct
//import org.springframework.data.redis.connection.Message
//import org.springframework.data.redis.connection.MessageListener
//import org.springframework.data.redis.core.RedisTemplate
//import org.springframework.data.redis.listener.ChannelTopic
//import org.springframework.data.redis.listener.RedisMessageListenerContainer
//import org.springframework.stereotype.Service
//import org.springframework.web.socket.TextMessage
//import org.springframework.web.socket.WebSocketSession
//import java.util.concurrent.ConcurrentHashMap
//
//@Service
//class RedisPubSubService(
//    private val redisTemplate: RedisTemplate<String, Any>,
//    private val listenerContainer: RedisMessageListenerContainer,
//    private val objectMapper: ObjectMapper, // Redis 메시지 페이로드를 파싱하기 위해 ObjectMapper 추가
//    private val gameLobbyService: LobbyService // 게임 로비 서비스 주입
//) {
//    private val sessions = ConcurrentHashMap<String, Pair<String, String>>() // key: sessionId, value: <roomId, userId>
//
//    fun addSession(roomId: String, userId: String, sessionId: String) {
//        sessions[sessionId] = Pair(roomId, userId)
//        println("Session added: $sessionId, User: $userId, Room: $roomId")
//    }
//
//    fun removeSession(sessionId: String) {
//        val info = sessions.remove(sessionId)
//
//        if (info != null) {
//            val (roomId, userId) = info
//            println("Processing disconnect for User: $userId in Room: $roomId")
//
//             gameLobbyService.leaveRoom(roomId.toLong(), userId.toLong()) // 필요하다면 이 로직 호출
//
//            val leaveMessage = mapOf(
//                "type" to "userLeft", "roomId" to roomId, "userId" to userId,
//                "message" to "$userId 님이 퇴장했습니다."
//            )
//            publish("chat.room.$roomId", leaveMessage)
//            println("User $userId left room $roomId. Notification published.")
//        }
//    }
//
//    // Redis로부터 메시지를 받아 해당 방의 모든 클라이언트에게 전송 (수정된 로직)
//    fun broadcastMessage(roomId: String, message: String) {
//        val textMessage = TextMessage(message)
//
////        // sessionInfo 맵을 순회하여 같은 roomId를 가진 세션들을 찾음
////        sessionInfo.forEach { (sessionId, infoPair) ->
////            val (currentRoomId, _) = infoPair
////            if (currentRoomId == roomId) {
////                val session = sessions[sessionId]
////                if (session != null && session.isOpen) {
////                    try {
////                        session.sendMessage(textMessage)
////                        println("Message sent to session $sessionId in room $roomId")
////                    } catch (e: Exception) {
////                        println("Error sending message to session $sessionId: ${e.message}")
////                    }
////                }
////            }
////        }
//    }
//
//    // Redis 채널 구독 설정
//    @PostConstruct
//    fun subscribeToRedisChannels() {
//        val messageListener = MessageListener { message: Message, pattern: ByteArray? ->
//            val receivedMessage = String(message.body)
//            println("Received message from Redis: $receivedMessage")
//
//            try {
//                // 메시지 페이로드에서 roomId 추출
//                val payload = objectMapper.readTree(receivedMessage)
//                val roomId = payload["roomId"].asText()
//
//                // Redis로부터 받은 메시지를 해당 방의 클라이언트들에게 전송
//                broadcastMessage(roomId, receivedMessage)
//            } catch (e: Exception) {
//                println("Error processing message from Redis: ${e.message}")
//            }
//        }
//
//        // 모든 채팅방 채널(`chat.room.*`)과 게임방 채널(`game.room.*`)을 와일드카드로 구독
//        val chatTopic = ChannelTopic("chat.room.*")
//        val gameTopic = ChannelTopic("game.room.*")
//        listenerContainer.addMessageListener(messageListener, listOf(chatTopic, gameTopic))
//        println("Subscribed to Redis channels: chat.room.*, game.room.*")
//    }
//
//    fun publish(channel: String, message: Any) {
//        val messagePayload = if (message is String) message else objectMapper.writeValueAsString(message)
//         redisTemplate.convertAndSend(channel, messagePayload)
//        println("Published to $channel: $messagePayload")
//    }
//}

package com.example.gameservice.redis_pubsub.service

import com.example.gameservice.gamelobby.service.LobbyService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

import java.util.concurrent.ConcurrentHashMap

@Service
class RedisPubSubService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val listenerContainer: RedisMessageListenerContainer,
    private val objectMapper: ObjectMapper,
    private val messagingTemplate: SimpMessagingTemplate, // STOMP 메시지 전송용
    private val gameLobbyService: LobbyService
) {

    // WebSocket 세션 관리용: key = sessionId, value = Pair(roomId, userId)
    private val sessions = ConcurrentHashMap<String, Pair<String, String>>()

    // 세션 추가
    fun addSession(roomId: String, userId: String, sessionId: String) {
        sessions[sessionId] = Pair(roomId, userId)
        println("✅ Session added: $sessionId, User: $userId, Room: $roomId")
    }

    // 세션 제거 및 퇴장 처리
    fun removeSession(sessionId: String) {
        val info = sessions.remove(sessionId)
        if (info != null) {
            val (roomId, userId) = info
            println("🚪 User $userId disconnected from Room $roomId")

            // 로비 서비스에서 퇴장 처리
            gameLobbyService.leaveRoom(roomId.toLong(), userId.toLong())

            // 퇴장 메시지 Redis에 발행
            val leaveMessage = mapOf(
                "type" to "userLeft",
                "roomId" to roomId,
                "userId" to userId,
                "message" to "$userId 님이 퇴장했습니다."
            )
            publish("game.room.$roomId", leaveMessage)
        }
    }

    // Redis로부터 메시지를 받아 STOMP 클라이언트에게 전송
    fun broadcastMessage(roomId: String, message: String) {
        println("📡 Broadcasting Redis message to STOMP clients in room $roomId")

        try {
            val jsonNode = objectMapper.readTree(message)
            val type = jsonNode["type"]?.asText() ?: "unknown"

            // type에 따라 대상 topic 결정
            val topic = when (type) {
                "chat" -> "/sub/chat/room/$roomId"
                "userJoined", "userLeft", "selectAi", "ready", "start" -> "/sub/game/room/$roomId"
                else -> "/sub/game/room/$roomId"
            }

            messagingTemplate.convertAndSend(topic, jsonNode)
            println("✅ Message sent to $topic")
        } catch (e: Exception) {
            println("❌ Error sending STOMP message: ${e.message}")
        }
    }

    // Redis 구독 시작
    @PostConstruct
    fun subscribeToRedisChannels() {
        val messageListener = MessageListener { message: Message, _: ByteArray? ->
            val receivedMessage = String(message.body)
            println("📨 Received from Redis: $receivedMessage")

            try {
                val payload = objectMapper.readTree(receivedMessage)
                val roomId = payload["roomId"]?.asText()
                if (roomId != null) {
                    broadcastMessage(roomId, receivedMessage)
                } else {
                    println("❌ roomId not found in message: $receivedMessage")
                }
            } catch (e: Exception) {
                println("❌ Error parsing Redis message: ${e.message}")
            }
        }

        // 채널 구독 (와일드카드는 직접 지원되지 않음, 동적 구독은 필요 시 구현)
        val chatTopic = ChannelTopic("chat.room.1") // 필요 시 다중 채널 추가
        val gameTopic = ChannelTopic("game.room.1") // 또는 전체 roomId를 subscribe 하도록 구조 변경

        listenerContainer.addMessageListener(messageListener, chatTopic)
        listenerContainer.addMessageListener(messageListener, gameTopic)

        println("🔔 Redis subscription started for chat.room.1 and game.room.1")
    }

    // Redis에 메시지 발행
    fun publish(channel: String, message: Any) {
        try {
            val messagePayload = if (message is String) message else objectMapper.writeValueAsString(message)
            redisTemplate.convertAndSend(channel, messagePayload)
            println("📤 Published to $channel: $messagePayload")
        } catch (e: Exception) {
            println("❌ Error publishing to Redis: ${e.message}")
        }
    }
}