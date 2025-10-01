package com.example.gameservice.game_result.service

import com.example.gameservice.game_result.dto.GameResultResponseDto
import com.example.gameservice.game_result.entity.GameDetailLog
import com.example.gameservice.game_result.entity.GameInfo
import com.example.gameservice.game_result.repository.GameDetailMongoRepository
import com.example.gameservice.game_result.repository.GameInfoMongoRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameResultServiceImpl(
    private val gameInfoMongoRepository: GameInfoMongoRepository,
    private val gameDetailMongoRepository: GameDetailMongoRepository
): GameResultService {
    override fun getGameResultByUserId(userId: Long): List<GameResultResponseDto> {
        // 1. 해당 userId가 포함된 모든 GameInfo 문서 조회
        // Repository 메서드 이름을 findByPlayerIdsIn(Long)으로 사용하도록 수정합니다.
        val gameInfoList: List<GameInfo> = gameInfoMongoRepository.findByPlayerIdsContains(userId)
            .filter { it.playerIds != null && it.aiIds != null } // null 체크

        if (gameInfoList.isEmpty()) {
            return emptyList()
        }

        // 2. 조회된 GameInfo ID들을 추출
        val gameInfoIds: List<UUID> = gameInfoList.mapNotNull { it.id }

        // 3. 해당 GameInfo ID들에 연결된 모든 GameDetailLog 조회
        val gameDetailLogList: List<GameDetailLog> = gameDetailMongoRepository.findByGameInfoIdIn(gameInfoIds)

        // 4. GameInfo와 GameDetailLog를 GameInfoId를 기준으로 맵핑
        // GameDetailLog를 GameInfoId를 키로 그룹화하여 맵을 생성
        val detailLogMap: Map<UUID, List<GameDetailLog>> =
            gameDetailLogList.filter { it.gameInfoId != null }
                .groupBy { it.gameInfoId!! }

        // 5. 최종 DTO 리스트 생성
        return gameInfoList.mapNotNull { gameInfo ->
            val details = detailLogMap[gameInfo.id]

            // 필수 데이터가 없으면 DTO 생성에서 제외
            if (details.isNullOrEmpty()) {
                null
            } else {
                // GameResultResponseDto.toDto 호출
                GameResultResponseDto.toDto(gameInfo, userId, details)
            }
        }
    }
}