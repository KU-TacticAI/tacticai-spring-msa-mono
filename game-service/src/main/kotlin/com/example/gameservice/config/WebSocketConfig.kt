package com.example.gameservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("http://localhost:3000")
            .withSockJS()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
                val accessor = StompHeaderAccessor.wrap(message)
                if (StompCommand.CONNECT == accessor.command) {
                    val bearer = accessor.getFirstNativeHeader("Authorization") // "Bearer xxx"
                    // TODO: JWT 파싱/검증 → 실패 시 예외 던지기 (CONNECT 에러로 전달됨)
                    // 성공 시 사용자 컨텍스트 부여
                    // accessor.user = UsernamePasswordAuthenticationToken(principal, null, authorities)
                }
                return message
            }
        })
    }
}