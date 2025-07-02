package com.example.gameservice.game_session.service

import com.example.gameservice.game_session.dto.GameSessionResponseDto
import com.example.gameservice.game_session.repository.GameDetailLogRepository
import com.example.gameservice.game_session.repository.GameSessionRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class GameSessionServiceImpl(
    private val gameSessionRepository: GameSessionRepository,
    private val gameDetailLogRepository: GameDetailLogRepository,
    private val webClient: WebClient,
    @Value("\${fastapi.base-url}")
    private val fastapiBaseUrl: String,
): GameSessionService {
    override fun getSnapShot(roomId: Long, id: Long): GameSessionResponseDto? {
//        TODO("Not yet implemented")
        val snapshot = gameDetailLogRepository
            .findTopByGameSessionIdOrderByIdDesc(id)
            ?: return null

        return GameSessionResponseDto.from(snapshot)
    }

    override fun receiveSnapShot(roomId: Long): GameSessionResponseDto? {
//        TODO("스냅샷을 전송하는 FAST API 서버에서 정보를 가져옴")
        return webClient.get()
            .uri("$fastapiBaseUrl/api/snapshot/{roomId}", roomId)
            .retrieve()
            .bodyToMono(GameSessionResponseDto::class.java)
            .block()
    }

    override fun getResult(roomId: String): GameSessionResponseDto? {
//        TODO("결과 정보를 FAST API 서버에서 가져옴")
//        TODO("결과 정보를 클라이언트에게 보여줌")
        return webClient.get()
            .uri("$fastapiBaseUrl/api/result/{roomId}", roomId)
            .retrieve()
            .bodyToMono(GameSessionResponseDto::class.java)
            .block()
    }
}