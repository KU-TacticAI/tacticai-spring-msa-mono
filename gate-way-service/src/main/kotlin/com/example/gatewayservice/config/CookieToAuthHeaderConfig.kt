package com.example.gatewayservice.config

import com.example.gatewayservice.util.JWTUtil
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

@Configuration
class CookieToAuthHeaderConfig(
    private val jwtUtil: JWTUtil
) {
    companion object {
        private const val REFRESH_COOKIE = "refresh"   // 실제 쿠키명
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun cookieToAuthHeaderFilter(): GlobalFilter = GlobalFilter { exchange, chain ->
        val req = exchange.request
        val path = req.uri.path

        // 리프레시 엔드포인트에서만 동작
        if (!path.contains("/token/refresh")) {
            return@GlobalFilter chain.filter(exchange)
        }

        val rawAuth = req.headers.getFirst(HttpHeaders.AUTHORIZATION).orEmpty()
        val refresh = req.cookies.getFirst(REFRESH_COOKIE)?.value

        // 1) Authorization 헤더가 없고, 쿠키가 있으면 → 헤더로 복사
        if (rawAuth.isBlank() && !refresh.isNullOrBlank()) {
            val mutated = exchange.mutate()
                .request(
                    req.mutate()
                        .headers { h -> h.set(HttpHeaders.AUTHORIZATION, "Bearer $refresh") } // set = 중복 방지
                        .build()
                ).build()
            return@GlobalFilter chain.filter(mutated)
        }

        chain.filter(exchange)
    }
}