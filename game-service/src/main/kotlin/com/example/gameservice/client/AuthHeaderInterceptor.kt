package com.example.gameservice.client

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class AuthHeaderInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val requestAttributes = RequestContextHolder.getRequestAttributes()
        if (requestAttributes is ServletRequestAttributes) {
            val servletRequest = requestAttributes.request
            val authHeader = servletRequest.getHeader("Authorization")
            if (authHeader != null && !request.headers.containsKey("Authorization")) {
                request.headers.add("Authorization", authHeader)
            }
        }
        return execution.execute(request, body)
    }
}
