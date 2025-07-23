package com.example.gatewayservice.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JWTUtil(
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${spring.jwt.secret}") private val secret: String
) {

    private lateinit var key: SecretKey

    @PostConstruct
    fun initKey() {
        val keyBytes = secret.toByteArray(StandardCharsets.UTF_8)
        require(keyBytes.size >= 32) { "Secret key must be at least 256 bits (32 bytes) for HS256" }
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun getUserId(token: String): String {
        return extractClaims(token)["userId"] as String
    }

    fun isExpired(token: String) {
        val expiration = extractClaims(token).expiration
        if (expiration.before(Date())) {
            throw ExpiredJwtException(null, null, "JWT expired")
        }
    }

    fun isIssuer(token: String) {
        val issuer = extractClaims(token).issuer
        if (issuer != TokenSettings.TOKEN_ISSUER) {
            throw IllegalArgumentException("잘못된 발급자입니다.")
        }
    }

    fun getCategory(token: String): String {
        return extractClaims(token)["category"] as String
    }

    fun createJwt(userId: String, category: String, expiredMs: Long): String {
        return Jwts.builder()
            .issuer(TokenSettings.TOKEN_ISSUER)
            .claim("userId", userId)
            .claim("category", category)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiredMs))
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    private fun extractClaims(token: String): Claims {
        return Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token)
            .payload
    }
}
