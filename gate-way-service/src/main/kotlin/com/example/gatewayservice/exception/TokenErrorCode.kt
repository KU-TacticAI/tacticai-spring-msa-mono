package com.example.gatewayservice.exception

import org.springframework.http.HttpStatus

enum class TokenErrorCode(
    override val httpStatus: HttpStatus,
    override val message: String
) : ExceptionType {
    TOKEN_CATEGORY_MISS_MATCH(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다: 카테고리 불일치"),
    REFRESH_TOKEN_MISS_MATCH(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다: 리플레시 토큰 불일치"),
    NO_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "다시 로그인해주시기 바랍니다.");

    override val errorName: String
        get() = name;
}
