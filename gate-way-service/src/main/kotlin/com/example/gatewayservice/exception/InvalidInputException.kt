package com.example.gatewayservice.exception

import org.springframework.http.HttpStatus

class InvalidInputException(
    private val exceptionType: ExceptionType
) : RuntimeException(exceptionType.message) {

    val httpStatus: HttpStatus = exceptionType.httpStatus;
    val errorName: String = exceptionType.errorName;
    override val message: String = exceptionType.message;
}