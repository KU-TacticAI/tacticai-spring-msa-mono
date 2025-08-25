package com.example.gameservice.client

import com.example.commonmodule.dto.AiUrlsResponseDto
import com.example.commonmodule.dto.PlayerResultDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

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

    fun getUserByIds(userIds: List<Long>): List<PlayerResultDto> {
        val url = UriComponentsBuilder.fromUriString("$coreServiceUrl/api/internal/users/list")
            .queryParam("ids", *userIds.toTypedArray()) // Kotlin의 Spread Operator 사용
            .build()
            .toUriString()

        val responseType = object : ParameterizedTypeReference<List<PlayerResultDto>>() {}

        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            responseType
        )

        return response.body ?: emptyList()
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

    fun getAiUrlById(aiId: Long?): AiUrlsResponseDto {
        if (aiId == null) {
            throw IllegalArgumentException("AI ID는 null일 수 없습니다.")
        }

        val url = "$coreServiceUrl/api/internal/ai/$aiId"

        return restTemplate.getForObject(url, AiUrlsResponseDto::class.java)
            ?: throw IllegalStateException("AI 정보를 찾을 수 없습니다: id=$aiId")
    }
}