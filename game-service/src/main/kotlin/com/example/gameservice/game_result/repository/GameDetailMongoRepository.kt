package com.example.gameservice.game_result.repository

import com.example.gameservice.common.GameStatus
import com.example.gameservice.game_result.entity.GameDetailLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameDetailMongoRepository : MongoRepository<GameDetailLog, UUID> {

    /**
     * 특정 게임(gameInfoId)에 속한 모든 상세 로그를 조회합니다.
     * @param gameInfoId 부모 게임의 ID
     * @return GameDetailLog 리스트
     */
    fun findByGameInfoId(gameInfoId: UUID): List<GameDetailLog>

    fun findByGameInfoIdIn(gameInfoIdList: List<UUID>): List<GameDetailLog>
}