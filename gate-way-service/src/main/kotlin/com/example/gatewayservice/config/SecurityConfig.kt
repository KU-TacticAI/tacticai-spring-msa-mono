package com.example.gatewayservice.config

import com.example.gatewayservice.filter.JWTFilter
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Suppress("SpringJavaInjectionPointsAutowiringInspection")

@Configuration
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val jwtFilter: JWTFilter
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowCredentials = true
            // 여러 환경/도메인 허용
            allowedOriginPatterns = listOf(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
            )
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization", "Location", "Link", "X-Total-Count", "Set-Cookie")
            maxAge = 3600
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cors { it.disable() }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // 🚩 웹소켓/SockJS 핸드셰이크와 폴백 경로 전부 허용
                    .pathMatchers(
                        "/ws/**",        // SockJS 기본
                        "/websocket",    // 직접 ws 업그레이드 경로
                        "/sockjs/**"     // 환경에 따라 생성되는 폴백 경로
                    ).permitAll()
                    .pathMatchers(
                        "/api/**",
                        "/email",
                        "/mail-check",
                        "/oauth2/**",
                        "/oauth2-login",
                        "/token/refresh",
                        "/refresh",
                        "/error",
                        "/api*",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/v3/**",
                        "/users/sign-in",
                        "/api/core/users/sign-in"
                    ).permitAll()
                    .anyExchange().authenticated()
            }
            .build()
    }

    @Bean
    fun globalFilter(): GlobalFilter = jwtFilter
}