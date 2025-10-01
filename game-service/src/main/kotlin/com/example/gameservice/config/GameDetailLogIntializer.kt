package com.example.gameservice.config

import com.example.gameservice.common.GameStatus
import com.example.gameservice.game.entity.Game
import com.example.gameservice.game_result.entity.GameDetailLog
import com.example.gameservice.game_result.repository.GameDetailMongoRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class GameDetailLogInitializer(
    private val gameDetailMongoRepository: GameDetailMongoRepository
) : CommandLineRunner {

    private val log = org.slf4j.LoggerFactory.getLogger(GameDetailLogInitializer::class.java)

    // GameInfoInitializer보다 늦게 실행되어도 무방하지만, 같이 실행되어야 데이터 연결이 됩니다.
    override fun run(vararg args: String?) {
        if (gameDetailMongoRepository.count() == 0L) {
            log.info("MongoDB 'game_detail_log' collection is empty. Initializing dummy log data...")

            // GameInfo ID는 GameInfoInitializer에서 사용된 ID와 일치해야 합니다.
            val gameInfoId1 = UUID.fromString("11111111-1111-1111-1111-111111111111")
            val gameInfoId2 = UUID.fromString("22222222-2222-2222-2222-222222222222")

            // --- GameInfo 1 (ID: 111...111) 로그 3건 ---
            val logsGame1 = listOf(
                // Log 1: 응답 시간 150ms, 턴 1
                GameDetailLog(
                    id = UUID.randomUUID(),
                    responseTimeMs = 150,
                    boardSnapshot = mapOf("board" to "initial"),
                    turnCount = 1,
                    moveData = "A1",
                    status = GameStatus.success,
                    logOutput = mapOf("log" to "move A1"),
                    aiId = 17L, // GameInfo 1의 playerIds[0] AI
                    gameInfoId = gameInfoId1
                ),
                // Log 2: 응답 시간 250ms, 턴 10
                GameDetailLog(
                    id = UUID.randomUUID(),
                    responseTimeMs = 250,
                    boardSnapshot = mapOf("board" to "mid"),
                    turnCount = 10,
                    moveData = "B5",
                    status = GameStatus.timeout,
                    logOutput = mapOf("log" to "move B5"),
                    aiId = 18L, // GameInfo 1의 playerIds[1] AI
                    gameInfoId = gameInfoId1
                ),
                // Log 3: 응답 시간 350ms, 턴 20 (최종)
                GameDetailLog(
                    id = UUID.randomUUID(),
                    responseTimeMs = 350,
                    boardSnapshot = mapOf("board" to "final"),
                    turnCount = 20,
                    moveData = "C8",
                    status = GameStatus.success,
                    logOutput = mapOf("log" to "final move C8"),
                    aiId = 17L,
                    gameInfoId = gameInfoId1
                )
            )

            // --- GameInfo 2 (ID: 222...222) 로그 2건 ---
            val logsGame2 = listOf(
                // Log 4: 응답 시간 500ms, 턴 1
                GameDetailLog(
                    id = UUID.randomUUID(),
                    responseTimeMs = 500,
                    boardSnapshot = mapOf("board" to "chess_start"),
                    turnCount = 1,
                    moveData = "E2-E4",
                    status = GameStatus.failed,
                    logOutput = mapOf("log" to "pawn move E2-E4"),
                    aiId = 19L, // GameInfo 2의 playerIds[0] AI
                    gameInfoId = gameInfoId2
                ),
                // Log 5: 응답 시간 100ms, 턴 5 (최종)
                GameDetailLog(
                    id = UUID.randomUUID(),
                    responseTimeMs = 100,
                    boardSnapshot = mapOf("board" to "chess_end"),
                    turnCount = 5,
                    moveData = "F7-F5",
                    status = GameStatus.success,
                    logOutput = mapOf("log" to "resign"),
                    aiId = 20L, // GameInfo 2의 playerIds[1] AI
                    gameInfoId = gameInfoId2
                )
            )

            gameDetailMongoRepository.saveAll(logsGame1 + logsGame2)
            log.info("Finished initializing 'game_detail_log' data. (5 documents inserted)")
        }
    }
}
