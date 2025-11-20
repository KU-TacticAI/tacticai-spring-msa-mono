package com.example.gameservice.config

import com.example.gameservice.game_result.entity.GameDetailLog
import com.example.gameservice.game_result.entity.GameInfo
import org.springframework.boot.CommandLineRunner
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.IndexOperations
import org.springframework.stereotype.Component
import org.springframework.data.mongodb.core.index.Index

@Component // 이 클래스를 Spring Bean으로 등록
class MongoCollectionInitializer(
    // Spring Data MongoDB의 핵심적인 저수준 작업을 담당하는 MongoTemplate를 주입받습니다.
    private val mongoTemplate: MongoTemplate
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        println("--- MongoDB 컬렉션 초기화 및 인덱스 보장 시작 ---")

        // 1. GameInfo 컬렉션 및 인덱스 확인/생성
        ensureCollectionAndIndexes(GameInfo::class.java)

        // 2. GameDetailLog 컬렉션 및 인덱스 확인/생성
        ensureCollectionAndIndexes(GameDetailLog::class.java)

        println("--- MongoDB 컬렉션 초기화 및 인덱스 보장 완료 ---")
    }

    /**
     * 컬렉션이 존재하는지 확인하고, 인덱스를 강제로 생성하는 통합 함수
     */
    private fun <T> ensureCollectionAndIndexes(entityClass: Class<T>) {
        val collectionName = mongoTemplate.getCollectionName(entityClass)

        // 1. 컬렉션 존재 확인 및 생성
        if (!mongoTemplate.collectionExists(entityClass)) {
            mongoTemplate.createCollection(entityClass)
            println("▶ 컬렉션 '$collectionName'이(가) 존재하지 않아 새로 생성했습니다.")
        } else {
            println("▶ 컬렉션 '$collectionName'이(가) 이미 존재합니다. 인덱스를 확인합니다.")
        }

        // 2. 인덱스 강제 생성 (DDL - CREATE INDEX 역할)
        val indexOps: IndexOperations = mongoTemplate.indexOps(entityClass)

        if (entityClass == GameInfo::class.java) {
            // GameInfo: player_ids 필드에 대한 인덱스 (사용자 ID 검색을 위해 필수)
            indexOps.ensureIndex(
                Index().on("player_ids", Sort.Direction.ASC).named("player_ids_idx")
            )
            println("   └ [GameInfo] player_ids 인덱스 생성 완료.")

        } else if (entityClass == GameDetailLog::class.java) {
            // GameDetailLog: gameinfo_id 필드에 대한 인덱스 (GameInfo와의 조회를 위해 필수)
            indexOps.ensureIndex(
                Index().on("gameinfo_id", Sort.Direction.ASC).named("gameinfo_id_idx")
            )
            println("   └ [GameDetailLog] gameinfo_id 인덱스 생성 완료.")
        }
    }
}