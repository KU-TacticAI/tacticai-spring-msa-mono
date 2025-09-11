package com.example.gatewayservice.filter

import com.example.gatewayservice.exception.TokenErrorCode
import com.example.gatewayservice.util.JWTUtil
import io.jsonwebtoken.ExpiredJwtException
import io.netty.handler.codec.http.HttpHeaderValidationUtil.validateToken
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
        val path = request.uri.path

        // Skip JWT validation for public endpoints like login, signup, and websockets
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange)
        }

        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Authorization header is missing or invalid")
        }

        val token = authHeader.removePrefix("Bearer ").trim()

        return try {
            validateToken(token)
            val userId = jwtUtil.getUserId(token)

            // Create a new request with the X-User-Id header
            val modifiedRequest = request.mutate()
                .header("X-User-Id", userId)
                .build()

            chain.filter(exchange.mutate().request(modifiedRequest).build())

        } catch (e: ExpiredJwtException) {
            unauthorized(exchange, "Token has expired")
        } catch (e: Exception) {
            unauthorized(exchange, "Invalid token")
        }
    }

    override fun getOrder(): Int = -1

    private fun unauthorized(exchange: ServerWebExchange, message: String): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON
//        val errorResponse = "{\"error\": \"${TokenErrorCode.NO_AUTHORIZATION.message}\"", \"message\": \"$message\"}"
        val errorResponse = "{\"error\": \"TokenErrorCode..message\", \"message\": \"$message\"}"
        val buffer = response.bufferFactory().wrap(errorResponse.toByteArray())
        return response.writeWith(Mono.just(buffer))
    }

    private fun isPublicEndpoint(path: String): Boolean {
        return path.startsWith("/ws") ||
               path.startsWith("/api/core/users/sign-in") ||
               path.startsWith("/api/core/login") ||
               path.startsWith("/api/core/token/refresh")
    }

    private fun validateToken(token: String) {
        jwtUtil.isExpired(token)
        jwtUtil.isIssuer(token)
    }
}

