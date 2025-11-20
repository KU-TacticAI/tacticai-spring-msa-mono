package com.example.gameservice.config

import com.example.gameservice.game_result.entity.GameInfo
import com.example.gameservice.game_result.repository.GameInfoMongoRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Order(1) // GameDetailLogInitializer보다 먼저 실행
class GameInfoInitializer(
    private val gameInfoMongoRepository: GameInfoMongoRepository
) : CommandLineRunner {

    private val log = org.slf4j.LoggerFactory.getLogger(GameInfoInitializer::class.java)

    override fun run(vararg args: String?) {
        if (gameInfoMongoRepository.count() == 0L) {
            log.info("MongoDB 'game_info' collection is empty. Initializing dummy game data...")

            val gameInfoList = listOf(
                // Game 1: Othello 게임
                GameInfo(
                    id = null, // MongoDB가 자동으로 ObjectId 생성
                    playerIds = listOf(1L, 2L),
                    aiIds = listOf(17L, 18L),
                    createdAt = LocalDateTime.now().minusDays(5),
                    gameType = "GameType.OTHELLO",
                    winnerAiId = 17L,
                    clientGameInfoId = "client-game-001"
                ),
                // Game 2: Chess 게임
                GameInfo(
                    id = null, // MongoDB가 자동으로 ObjectId 생성
                    playerIds = listOf(3L, 4L),
                    aiIds = listOf(19L, 20L),
                    createdAt = LocalDateTime.now().minusDays(3),
                    gameType = "GameType.CHESS",
                    winnerAiId = 20L,
                    clientGameInfoId = "client-game-002"
                ),
                // Game 3: 진행중인 게임 (승자 없음)
                GameInfo(
                    id = null, // MongoDB가 자동으로 ObjectId 생성
                    playerIds = listOf(5L, 6L),
                    aiIds = listOf(21L, 22L),
                    createdAt = LocalDateTime.now().minusHours(2),
                    gameType = "GameType.OTHELLO",
                    winnerAiId = null, // 아직 진행중
                    clientGameInfoId = "client-game-003"
                )
            )

            val savedGameInfoList = gameInfoMongoRepository.saveAll(gameInfoList)
            log.info("Finished initializing 'game_info' data. (${savedGameInfoList.size} documents inserted)")

            // 저장된 게임 ID 로깅 (GameDetailLogInitializer에서 참조할 수 있도록)
            savedGameInfoList.forEachIndexed { index, gameInfo ->
                log.info("Game ${index + 1} saved with ID: ${gameInfo.id}")
            }
        }
    }
}