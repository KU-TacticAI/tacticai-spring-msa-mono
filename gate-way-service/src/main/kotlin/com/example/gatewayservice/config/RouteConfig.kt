package com.example.gatewayservice.config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RouteConfig {

    @Bean
    fun gatewayRoutes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("core-service") { r ->
                r.path("/api/core/**")
                    .filters{it.stripPrefix(2)}
                    .uri("http://localhost:8081")
            }
            .route("game-service") { r ->
                r.path("/api/game/**")
                    .filters{it.stripPrefix(2)}
                    .uri("http://localhost:8082")
            }
            .route("game-ws") { r ->
                r.path("/ws/**")
                    .filters { f ->
                        f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                            .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                    }
                    .uri("http://localhost:8082")
            }
            .build()
    }
}