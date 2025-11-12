package com.example.gameservice.config

import com.example.commonmodule.util.JWTUtil
import com.example.gameservice.filter.JwtAuthorizationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtUtil: JWTUtil) {

    @Bean
    @Order(1) // ⭐️ 1. WebSocket용 필터 체인 (1순위)
    fun webSocketSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/ws/**") // ⭐️ /ws/** 경로에만 적용
            .authorizeHttpRequests {
                it.anyRequest().permitAll() // /ws/** 모든 요청 허용
            }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        // ⭐️ 중요: 여기엔 JWT 필터가 없습니다.

        return http.build()
    }

    @Bean
    @Order(2) // ⭐️ 2. 나머지 API용 필터 체인 (2순위)
    fun apiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/**") // ⭐️ /ws/** 를 제외한 모든 경로에 적용
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.anyRequest().hasAuthority("USER")
            }
            // ⭐️ 여기에만 JWT 필터 추가
            .addFilterBefore(JwtAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}