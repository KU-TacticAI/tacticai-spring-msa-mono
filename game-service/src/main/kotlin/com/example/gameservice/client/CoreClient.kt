package com.example.gameservice.client

import com.example.commonmodule.dto.AiUrlsResponseDto
import com.example.commonmodule.dto.PlayerResultDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

@Component
class CoreClient(
    restTemplateBuilder: RestTemplateBuilder,
    @Value("\${core.service.url}")
    private val coreServiceUrl: String
) {
    private val restTemplate = restTemplateBuilder.build()

    fun getUserById(userId: Long): PlayerResultDto {
        val url = "$coreServiceUrl/api/internal/users/$userId"
        return restTemplate.getForObject(url, PlayerResultDto::class.java)
            ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다: id=$userId")
    }

    fun getAiUrlsByIds(aiIds: List<Long?>): List<AiUrlsResponseDto> {
        val joinedParams = aiIds.joinToString("&") { "ids=$it" }
        val url = "$coreServiceUrl/api/internal/ai/list?$joinedParams"

        val responseType = object : ParameterizedTypeReference<List<AiUrlsResponseDto>>() {}

        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            responseType
        )

        return response.body ?: emptyList()
    }
}