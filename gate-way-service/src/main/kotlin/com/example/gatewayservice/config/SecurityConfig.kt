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
            allowedOrigins = listOf("http://localhost:3000", "http://127.0.0.1:3000")
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization", "Location", "Link", "X-Total-Count")
            maxAge = 3600
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cors { }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
                        // 🔽 sign-in 경로는 명시적으로
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