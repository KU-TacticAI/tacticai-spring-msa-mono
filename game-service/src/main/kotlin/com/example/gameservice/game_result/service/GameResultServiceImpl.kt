package com.example.gameservice.game_result.service

import com.example.gameservice.game_result.dto.GameResultResponseDto
import com.example.gameservice.game_result.entity.GameDetailLog
import com.example.gameservice.game_result.entity.GameInfo
import com.example.gameservice.game_result.repository.GameDetailMongoRepository
import com.example.gameservice.game_result.repository.GameInfoMongoRepository
import org.springframework.stereotype.Service

@Service
class GameResultServiceImpl(
    private val gameInfoMongoRepository: GameInfoMongoRepository,
    private val gameDetailMongoRepository: GameDetailMongoRepository
): GameResultService {
    override fun getGameResultByUserId(userId: Long): List<GameResultResponseDto> {
        // 1. 해당 userId가 포함된 모든 GameInfo 문서 조회
        val gameInfoList: List<GameInfo> = gameInfoMongoRepository.findByPlayerIdsContains(userId)
            .filter { it.playerIds != null && it.aiIds != null }

        if (gameInfoList.isEmpty()) {
            return emptyList()
        }

        // [수정 1] MongoDB의 _id(it.id)가 아니라, clientGameInfoId(UUID)를 추출해야 합니다.
        val gameInfoIds: List<String> = gameInfoList.mapNotNull { it.clientGameInfoId }

        // 3. 해당 UUID들에 연결된 모든 GameDetailLog 조회
        // (DB의 gameinfo_id 필드에 UUID가 저장되어 있으므로 정상 조회됨)
        val gameDetailLogList: List<GameDetailLog> = gameDetailMongoRepository.findByGameInfoIdIn(gameInfoIds)

        // 4. GameDetailLog를 gameInfoId(UUID)를 기준으로 그룹화
        val detailLogMap: Map<String, List<GameDetailLog>> =
            gameDetailLogList.filter { it.gameInfoId != null }
                .groupBy { it.gameInfoId!! }

        // 5. 최종 DTO 리스트 생성
        return gameInfoList.mapNotNull { gameInfo ->
            // [수정 2] 맵에서 꺼낼 때도 gameInfo.id가 아닌 gameInfo.clientGameInfoId로 꺼내야 합니다.
            // (gameInfo.clientGameInfoId가 null일 경우 safe call 처리)
            val key = gameInfo.clientGameInfoId
            if (key == null) return@mapNotNull null

            val details = detailLogMap[key]

            // 필수 데이터가 없으면 DTO 생성에서 제외
            if (details.isNullOrEmpty()) {
                null
            } else {
                GameResultResponseDto.toDto(gameInfo, userId, details)
            }
        }
    }
}