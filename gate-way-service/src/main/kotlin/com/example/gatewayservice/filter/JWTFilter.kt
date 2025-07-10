package com.example.gatewayservice.filter

import com.example.gatewayservice.exception.InvalidInputException
import com.example.gatewayservice.exception.TokenErrorCode
import com.example.gatewayservice.util.JWTUtil
import com.example.gatewayservice.util.TokenSettings
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JWTFilter(
    private val jwtUtil: JWTUtil
) : GlobalFilter, Ordered {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val response = exchange.response
        val uri = request.uri.path

        if (isLoginRequest(uri) || isSignInRequest(uri)) {
            return chain.filter(exchange)
        }

        val accessToken = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            ?.removePrefix("Bearer ")
            ?: return chain.filter(exchange)

        return try {
            authenticateUser(accessToken)
            validateToken(accessToken)
            chain.filter(exchange)
        } catch (e: ExpiredJwtException) {
            val refreshToken = request.cookies.getFirst(TokenSettings.REFRESH_TOKEN_CATEGORY)?.value
            return try {
                if (refreshToken != null) {
                    authenticateUser(refreshToken)
                    validateToken(refreshToken)

                    // TODO: 필요시 새 토큰 발급 처리 추가

                    chain.filter(exchange)
                } else {
                    unauthorized(response, "리프레시 토큰이 없습니다.")
                }
            } catch (ex: Exception) {
                unauthorized(response, "잘못된 리프레시 토큰입니다.")
            }
        } catch (e: Exception) {
            unauthorized(response, "잘못된 토큰입니다.")
        }
    }

    override fun getOrder(): Int = -1

    private fun unauthorized(response: org.springframework.http.server.reactive.ServerHttpResponse, message: String): Mono<Void> {
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.TEXT_PLAIN
        val buffer = response.bufferFactory().wrap(message.toByteArray(Charsets.UTF_8))
        return response.writeWith(Mono.just(buffer))
    }

    private fun isLoginRequest(uri: String): Boolean {
        return uri.matches(Regex(".*/login(?:/.*)?$")) || uri.matches(Regex(".*/oauth2(?:/.*)?$"))
    }

    private fun isSignInRequest(uri: String): Boolean {
        return uri.matches(Regex(".*/sign-in(?:/.*)?$"))
    }

    private fun validateToken(token: String) {
        jwtUtil.isExpired(token)
        jwtUtil.isIssuer(token)
        val category = jwtUtil.getCategory(token)
        if (category != TokenSettings.ACCESS_TOKEN_CATEGORY) {
            throw InvalidInputException(TokenErrorCode.TOKEN_CATEGORY_MISS_MATCH)
        }
    }

    private fun authenticateUser(token: String) {
        val userId = jwtUtil.getUserId(token).toLong()
        // TODO: 필요시 사용자 ID를 Request Header에 담아 downstream에 전달
    }
}
