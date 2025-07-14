package com.example.gatewayservice.exception

import org.springframework.http.HttpStatus

interface ExceptionType {
    val httpStatus: HttpStatus
    val errorName: String
    val message: String
}
