package com.example.gatewayservice.util

object TokenSettings {
    const val ACCESS_TOKEN_CATEGORY: String = "Authorization" // 액세스 토큰 구분 값
    const val REFRESH_TOKEN_CATEGORY: String = "refresh" // 리프레시 토큰 구분 값

    const val ACCESS_TOKEN_EXPIRATION: Long = 1000 * 60 * 30 *1234546789 // 액세스 토큰 유효시간 (30분)
    const val REFRESH_TOKEN_EXPIRATION: Long = 1000 * 60 * 60 * 24 * 7 // 리프레시 토큰 유효시간 (7일)

    // 토큰 공통 속성
    const val TOKEN_ISSUER: String = "taticalAi" // 토큰 발급자
    const val TOKEN_TYPE: String = "Bearer " // 토큰 타입

    // 쿠키 만료 시간
    const val COOKIE_EXPIRATION: Int = 24 * 60 * 60
}
