package com.example.gatewayservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RouteConfig(
    @Value("\${core.service.url}")
    private val coreServiceUrl: String,
    @Value("\${game.service.url}")
    private val gameServiceUrl: String,
    @Value("\${socket.service.url}")
    private val socketServiceUrl: String
) {

    @Bean
    fun gatewayRoutes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("core-service") { r ->
                r.path("/api/core/**")
                    .filters{it.stripPrefix(2)}
                    .uri(coreServiceUrl)
            }
            // WebSocket 라우트 - 순서 중요! (일반 game-service보다 먼저 와야 함)
            .route("game-ws") { r ->
                r.path("/api/game/ws/**")
                    .filters { f ->
                        // stripPrefix 제거 - WebSocket 핸드셰이크 경로 유지
                        f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                            .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                    }
                    // lb:ws:// 형식으로 WebSocket 프로토콜 명시
                    .uri(socketServiceUrl.replace("http://", "lb:ws://")
                        .replace("https://", "lb:wss://"))
            }
            .route("game-service") { r ->
                r.path("/api/game/**")
                    .filters { it.stripPrefix(2) }
                    .uri(gameServiceUrl)
            }
            .build()
    }
}