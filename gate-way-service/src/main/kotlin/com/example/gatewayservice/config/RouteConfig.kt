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
        // WebSocket URL 처리 - 환경변수가 Game Service와 같을 경우 처리
        val wsServiceUrl = if (socketServiceUrl == gameServiceUrl) {
            // 같은 서비스면 그대로 사용
            gameServiceUrl
        } else {
            // 다른 서비스면 socket URL 사용
            socketServiceUrl
        }

        return builder.routes()
            // Core Service 라우트
            .route("core-service") { r ->
                r.path("/api/core/**")
                    .filters { it.stripPrefix(2) }
                    .uri(coreServiceUrl)
            }
            // WebSocket 라우트 - 가장 먼저 매치되도록 배치
            .route("game-ws-sockjs") { r ->
                r.path("/api/game/ws/**")  // SockJS의 모든 엔드포인트 포함
                    .filters { f ->
                        // stripPrefix 사용하지 않음 - 전체 경로 유지
                        f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                            .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                    }
                    .uri(wsServiceUrl)  // HTTP/HTTPS로 라우팅 (SockJS가 업그레이드 처리)
            }
            // 일반 Game Service API 라우트
            .route("game-service") { r ->
                r.path("/api/game/**")
                    .and()
                    .not { it.path("/api/game/ws/**") }  // WebSocket 경로 제외
                    .filters { it.stripPrefix(2) }
                    .uri(gameServiceUrl)
            }
            .build()
    }
}