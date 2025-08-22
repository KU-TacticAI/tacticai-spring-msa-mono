package com.example.gameservice.redis_pubsub.service

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Service
class RedisPubSubService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val listenerContainer: RedisMessageListenerContainer,
    private val objectMapper: ObjectMapper // Redis 메시지 페이로드를 파싱하기 위해 ObjectMapper 추가
) {
    // 세션 관리를 RedisPubSubService 내부에서 직접 처리
    private val sessions = ConcurrentHashMap<String, MutableSet<WebSocketSession>>()

    // 클라이언트의 세션을 추가/제거하는 메서드
    fun addSession(roomId: String, session: WebSocketSession) {
        sessions.getOrPut(roomId) { ConcurrentHashMap.newKeySet() }.add(session)
    }

    fun removeSession(session: WebSocketSession) {
        sessions.values.forEach { it.remove(session) }
    }

    // 메시지 발행 (Publish)
    fun publish(channel: String, message: String) {
        redisTemplate.convertAndSend(channel, message)
    }

    // Redis로부터 메시지를 받아 클라이언트에게 전송
    fun broadcastMessage(roomId: String, message: String) {
        val sessionsInRoom = sessions[roomId]
        if (sessionsInRoom != null) {
            val textMessage = TextMessage(message)
            sessionsInRoom.forEach { session ->
                if (session.isOpen) {
                    session.sendMessage(textMessage)
                }
            }
        }
    }

    @PostConstruct
    fun subscribeToRedisChannels() {
        val messageListener = MessageListener { message: Message, pattern: ByteArray? ->
            val receivedMessage = String(message.body)
            println("Received message from Redis: $receivedMessage")

            // 메시지 페이로드에서 roomId 추출
            val payload = objectMapper.readTree(receivedMessage)
            val roomId = payload["roomId"].asText()

            // Redis로부터 받은 메시지를 해당 방의 클라이언트들에게 전송
            broadcastMessage(roomId, receivedMessage)
        }

        val topic = ChannelTopic("chat.room.*") // 모든 채팅방 채널을 와일드카드로 구독
        listenerContainer.addMessageListener(messageListener, topic)
    }
}