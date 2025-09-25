package com.example.gameservice.game_result.entity

import com.example.gameservice.common.GameStatus
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "game_detail_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
data class GameDetailLog(

    @Id
    val id: UUID,

    @Field("response_time_ms")
    val responseTimeMs: Int?,

    // JSON 필드는 특정 구조가 없다면 Map 이나 Any 타입으로 매핑하는 것이 유연합니다.
    @Field("board_snapshot")
    val boardSnapshot: Map<String, Any>?,

    @Field("turn_count")
    val turnCount: Int?,

    @Field("move_data")
    val moveData: String?,

    @Field("execution_time_ms")
    val executionTimeMs: Int?,

    // ENUM 타입은 Kotlin의 enum class로 매핑하여 타입 안정성을 높이는 것이 좋습니다.
    @Field("status")
    val status: GameStatus?,

    @Field("log_output")
    val logOutput: Map<String, Any>?,

    @Field("ai_id")
    val aiId: Long?,

    // gameinfo 테이블과의 관계를 나타내는 외래 키
    @Field("gameinfo_id")
    val gameInfoId: UUID?
)
