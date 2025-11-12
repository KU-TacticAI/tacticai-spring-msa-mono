package com.example.gameservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // 메시지 브로커가 /topic으로 시작하는 주소를 구독하는 클라이언트들에게 메시지를 전달하도록 설정
        config.enableSimpleBroker("/topic")
        // 클라이언트에서 서버로 메시지를 보낼 때 사용하는 주소의 접두사 설정
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // 웹소켓 연결을 위한 엔드포인트 설정
        // 클라이언트는 /ws 주소로 STOMP 연결을 시도
        registry.addEndpoint("/api/game/ws")
//            .setAllowedOrigins(
//                "https://tacticai.site",
//                "http://localhost:3000")
            .setAllowedOriginPatterns("*")
            .withSockJS() // SockJS를 사용하여 웹소켓을 지원하지 않는 브라우저에서도 통신 가능하도록 함
    }
}
