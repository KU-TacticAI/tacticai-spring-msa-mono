package com.example.gameservice.game_result.repository

import com.example.gameservice.game_result.entity.GameInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameInfoMongoRepository : MongoRepository<GameInfo, UUID> {

    /**
     * 특정 게임 타입(gameType)에 해당하는 모든 게임 정보를 조회합니다.
     * @param gameType 조회할 게임 타입
     * @return GameInfo 리스트
     */
    fun findByGameType(gameType: String): List<GameInfo>

    /**
     * playerIds 배열에 특정 플레이어 ID가 포함된 모든 게임 정보를 조회합니다.
     * @param playerId 포함 여부를 확인할 플레이어 ID
     * @return GameInfo 리스트
     */
    fun findByPlayerIdsContains(playerId: String): List<GameInfo>
}