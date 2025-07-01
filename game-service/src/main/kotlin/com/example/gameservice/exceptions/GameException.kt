package com.example.gameservice.exceptions

import com.example.commonmodule.exceptions.ExceptionType
import lombok.Getter
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus

@Getter
@RequiredArgsConstructor
enum class GameException(
    private val httpStatus: HttpStatus,
    private val message: String
) : ExceptionType {

    DELETED_AI(HttpStatus.BAD_REQUEST, "이미 삭제된 AI입니다."),
    NOT_FOUND_AI(HttpStatus.NOT_FOUND, "AI 를 찾을 수 없습니다.");

    override fun getHttpStatus(): HttpStatus = httpStatus
    override fun getMessage(): String = message
    override fun getErrorName(): String = name
}