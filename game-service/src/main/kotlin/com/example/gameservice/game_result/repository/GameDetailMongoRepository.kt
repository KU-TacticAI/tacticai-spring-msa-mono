package com.example.gameservice.game_result.repository

import com.example.gameservice.game_result.entity.GameDetailLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface GameDetailMongoRepository : MongoRepository<GameDetailLog, String> {

    /**
     * 특정 게임(gameInfoId)에 속한 모든 상세 로그를 조회합니다.
     * @param gameInfoId 부모 게임의 ID (MongoDB ObjectId 문자열)
     * @return GameDetailLog 리스트
     */
    fun findByGameInfoId(gameInfoId: String): List<GameDetailLog>

    /**
     * 여러 게임의 상세 로그를 조회합니다.
     * @param gameInfoIdList 게임 ID 리스트 (MongoDB ObjectId 문자열 리스트)
     * @return GameDetailLog 리스트
     */
    fun findByGameInfoIdIn(gameInfoIdList: List<String>): List<GameDetailLog>
}