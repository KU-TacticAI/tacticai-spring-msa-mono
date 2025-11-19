package com.example.gameservice.game_result.repository

import com.example.gameservice.game_result.entity.GameInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface GameInfoMongoRepository : MongoRepository<GameInfo, String> {

    /**
     * playerIds 배열에 특정 플레이어 ID가 포함된 모든 게임 정보를 조회합니다.
     * @param playerId 포함 여부를 확인할 플레이어 ID
     * @return GameInfo 리스트
     */
    fun findByPlayerIdsContains(playerId: Long): List<GameInfo>

    /**
     * aiIds 배열에 특정 AI ID가 포함된 게임 정보를 조회합니다.
     * @param aiId 포함 여부를 확인할 AI ID
     * @return GameInfo 리스트
     */
    fun findByAiIdsContains(aiId: Long): List<GameInfo>

    /**
     * 클라이언트 게임 ID로 게임 정보를 조회합니다.
     * @param clientGameInfoId 클라이언트에서 생성한 게임 ID
     * @return GameInfo 또는 null
     */
    fun findByClientGameInfoId(clientGameInfoId: String): GameInfo?

    /**
     * 게임 타입으로 게임 정보를 조회합니다.
     * @param gameType 게임 타입 (예: "GameType.OTHELLO")
     * @return GameInfo 리스트
     */
    fun findByGameType(gameType: String): List<GameInfo>

    /**
     * 특정 AI가 승리한 게임을 조회합니다.
     * @param winnerAiId 승리한 AI의 ID
     * @return GameInfo 리스트
     */
    fun findByWinnerAiId(winnerAiId: Long): List<GameInfo>

    /**
     * 날짜 범위로 게임을 조회합니다.
     * @param start 시작 날짜/시간
     * @param end 종료 날짜/시간
     * @return GameInfo 리스트
     */
    fun findByCreatedAtBetween(start: LocalDateTime, end: LocalDateTime): List<GameInfo>

    /**
     * 특정 플레이어와 특정 AI가 함께 플레이한 게임을 조회합니다.
     * @param playerId 플레이어 ID
     * @param aiId AI ID
     * @return GameInfo 리스트
     */
    fun findByPlayerIdsContainsAndAiIdsContains(playerId: Long, aiId: Long): List<GameInfo>
}