package com.example.gameservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig (
    @Value("\${fastapi.base-url}")
    private val fastapiBaseUrl: String,
){
    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl(fastapiBaseUrl)
            .build()
    }
}