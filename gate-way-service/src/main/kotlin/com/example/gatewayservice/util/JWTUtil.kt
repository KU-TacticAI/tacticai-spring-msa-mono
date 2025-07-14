package com.example.gatewayservice.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JWTUtil(
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${spring.jwt.secret}") private val secret: String
) {

    private lateinit var key: SecretKey

    @PostConstruct
    fun initKey() {
        key = SecretKeySpec(
            secret.toByteArray(StandardCharsets.UTF_8),
            SignatureAlgorithm.HS256.jcaName
        )
    }

    // JWT에서 사용자 ID 추출
    fun getUserId(token: String): String {
        return extractClaims(token).subject
    }

    // JWT의 만료 여부 검사
    fun isExpired(token: String) {
        val expiration = extractClaims(token).expiration
        if (expiration.before(Date())) {
            throw ExpiredJwtException(null, null, "JWT expired")
        }
    }

    // JWT의 발급자 확인
    fun isIssuer(token: String) {
        val issuer = extractClaims(token).issuer
        if (issuer != TokenSettings.TOKEN_ISSUER) {
            throw IllegalArgumentException("잘못된 발급자입니다.")
        }
    }

    // JWT의 category 확인
    fun getCategory(token: String): String {
        return extractClaims(token)["category"] as String
    }

    // JWT 생성
    fun createJwt(userId: String, category: String, expiredMs: Long): String {
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiredMs))
            .setIssuer(TokenSettings.TOKEN_ISSUER)
            .claim("category", category)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    // JWT Claims 추출
    private fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
