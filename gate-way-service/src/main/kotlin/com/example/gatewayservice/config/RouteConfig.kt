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
            .route("game-ws") { r ->
                r.path("/api/game/ws/**")
                    .filters { f ->
                        f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                            .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                    }
                    .uri(socketServiceUrl)
            }
            .route("game-service") { r ->
                r.path("/api/game/**")
                    .filters { it.stripPrefix(2) }
                    .uri(gameServiceUrl)
            }
            .build()
    }
}