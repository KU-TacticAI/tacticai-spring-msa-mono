package com.example.gameservice.game_result.dto

import com.example.gameservice.game_result.entity.GameDetailLog
import com.example.gameservice.game_result.entity.GameInfo
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDateTime

@Getter
@AllArgsConstructor
@NoArgsConstructor
data class GameResultResponseDto(
    val id: String?, // MongoDB ObjectId (문자열)
    val playerId: Long, // player_id -> playerId 로 수정
    val enemyId: Long, // enemy_id -> enemyId 로 수정
    val aiId: Long?, // ai_id -> aiId 로 수정 (Nullable)
    val gameType: String?, // game_type -> gameType 로 수정
    val winnerAiId: Long?,
    val responseTimeMs: Int,
    val turnCount: Int,
    val moveData: String?, // move_data
    val isWin: Boolean,
    val createdAt: LocalDateTime?,
) {
    companion object {
        fun toDto(gameInfo: GameInfo, userId: Long, gameDetailLog: List<GameDetailLog>): GameResultResponseDto {

            // 1. 현재 사용자(userId)의 인덱스를 찾습니다. (playerIds의 0 또는 1)
            val userIndex = gameInfo.playerIds?.indexOf(userId) ?:
            throw IllegalStateException("User ID $userId not found in GameInfo ${gameInfo.id}")

            // 2. 평균 응답 시간과 최종 턴 수를 계산합니다.
            val avgResponseTime = gameDetailLog.mapNotNull { it.responseTimeMs }.average().toInt()
            val maxTurnCount = gameDetailLog.maxOfOrNull { it.turnCount ?: 0 } ?: 0

            // 3. 가장 마지막 턴의 moveData를 가져옵니다. (단순화를 위해 마지막 로그 사용)
            val lastMoveData = gameDetailLog.maxByOrNull { it.turnCount ?: 0 }?.moveData

            val isWin = gameInfo.aiIds?.get(userIndex) == gameInfo.winnerAiId

            // 4. DTO 생성 및 반환
            return GameResultResponseDto(
                id = gameInfo.id,
                playerId = gameInfo.playerIds[userIndex],
                enemyId = gameInfo.playerIds[1 - userIndex], // 상대방 ID (0이면 1, 1이면 0)
                aiId = gameInfo.aiIds?.getOrNull(userIndex), // AI ID는 Long? 타입
                gameType = gameInfo.gameType,
                winnerAiId = gameInfo.winnerAiId,
                responseTimeMs = avgResponseTime, // 평균 응답 시간
                turnCount = maxTurnCount, // 최종 턴 수
                moveData = lastMoveData, // 최종 무브 데이터
                isWin = isWin,
                createdAt = gameInfo.createdAt
            )
        }
    }
}