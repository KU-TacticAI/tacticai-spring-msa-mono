package com.example.gatewayservice.exception

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class GlobalErrorHandler(
    private val objectMapper: ObjectMapper = ObjectMapper()
) : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val response = exchange.response

        val (status, errorName, message) = when (ex) {
            is InvalidInputException -> Triple(
                ex.httpStatus,
                ex.errorName,
                ex.message ?: "Invalid input"
            )
            else -> Triple(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                ex.message ?: "Unexpected error"
            )
        }

        if (response.isCommitted) {
            return Mono.error(ex)
        }

        response.statusCode = status
        response.headers.contentType = MediaType.APPLICATION_JSON

        val errorBody = mapOf(
            "error" to errorName,
            "message" to message,
            "path" to exchange.request.uri.path,
            "status" to status.value()
        )

        val buffer: DataBuffer = response.bufferFactory().wrap(
            objectMapper.writeValueAsBytes(errorBody)
        )

        return response.writeWith(Mono.just(buffer))
    }
}
