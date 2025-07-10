package com.example.gatewayservice.exception

import org.springframework.http.HttpStatus

class InvalidInputException(
    private val exceptionType: ExceptionType
) : RuntimeException(exceptionType.getMessage()) {

    val httpStatus: HttpStatus = exceptionType.getHttpStatus();
    val errorName: String = exceptionType.getErrorName();
    override val message: String = exceptionType.getMessage()
}