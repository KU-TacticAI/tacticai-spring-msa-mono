package com.example.gameservice.game_session.service

import com.example.commonmodule.dto.GameResultResponseDto
import com.example.commonmodule.dto.PlayerResultDto
import com.example.gameservice.client.CoreClient
import com.example.gameservice.game_session.dto.GameProgressDto
import com.example.gameservice.game_session.dto.GameRequestDto
import com.example.gameservice.game_session.dto.GameSessionResponseDto
import com.example.gameservice.game_session.entity.GameResult
import com.example.gameservice.game_session.repository.GameDetailLogMySqlRepository
import com.example.gameservice.game_session.repository.GameResultRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class GameSessionServiceImpl(
    private val gameDetailLogMySqlRepository: GameDetailLogMySqlRepository,
    private val webClient: WebClient,
    @Value("\${fastapi.base-url}")
    private val fastapiBaseUrl: String,
    private val gameResultRepository: GameResultRepository,
    private val coreClient: CoreClient,
) : GameSessionService {

    /**
     * DB에서 가장 최근의 게임 스냅샷을 조회합니다.
     * @param roomId 방 ID (사용되지 않음)
     * @param id GameSession ID
     */
    override fun getSnapShot(roomId: Long, id: Long): GameSessionResponseDto? {
        val snapshot = gameDetailLogMySqlRepository
            .findTopByGameSessionIdOrderByIdDesc(id)
            ?: return null

        return GameSessionResponseDto.from(snapshot)
    }

    /**
     * FastAPI 서버로부터 최신 게임 스냅샷 정보를 가져옵니다.
     * GET /api/snapshot/{roomId}
     */
    override fun receiveSnapShot(roomId: Long): GameSessionResponseDto? {
        return webClient.get()
            .uri("$fastapiBaseUrl/api/snapshot/{roomId}", roomId)
            .retrieve()
            .bodyToMono(GameSessionResponseDto::class.java)
            .block()
    }

    override fun showResult(roomId: String): GameResultResponseDto? {
        val gameResult: GameResult = gameResultRepository.findByRoomId(roomId.toLong())
        val winnerUser: PlayerResultDto = coreClient.getUserById(gameResult.winnerId!!)

        return GameResultResponseDto.toDto(gameResult.roomId, winnerUser)
    }

    /**
     * FastAPI 서버로부터 게임 결과 정보를 가져옵니다.
     * GET /api/result/{roomId}
     */
    fun getResult(roomId: String): GameResultResponseDto? {
        return webClient.get()
            .uri("$fastapiBaseUrl/api/result/{roomId}", roomId)
            .retrieve()
            .bodyToMono(GameResultResponseDto::class.java)
            .block()
    }

    /**
     * FastAPI 서버로 게임 요청을 전송합니다.
     * POST /game-request
     * @param requestDto 게임 요청 DTO
     * @return 요청 성공 여부
     */
    fun sendGameRequest(requestDto: GameRequestDto): Boolean {
        return webClient.post()
            .uri("$fastapiBaseUrl/game-request")
            .bodyValue(requestDto)
            .retrieve()
            .bodyToMono(Void::class.java)
            .map { true }
            .onErrorReturn(false)
            .block() ?: false
    }

    /**
     * FastAPI 서버에서 특정 게임의 진행 상황을 조회합니다.
     * GET /progress/{game_id}?n={n}
     * @param gameId 게임 ID
     * @param n 조회할 메시지 수 (기본값: 10)
     * @return 게임 진행 상태 리스트
     */
    fun getProgress(gameId: String, n: Int = 10): List<GameProgressDto> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("$fastapiBaseUrl/progress/{gameId}")
                    .queryParam("n", n)
                    .build(gameId)
            }
            .retrieve()
            .bodyToFlux(GameProgressDto::class.java)
            .collectList()
            .block() ?: emptyList()
    }
}
