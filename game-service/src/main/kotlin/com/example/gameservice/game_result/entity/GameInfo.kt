package com.example.gameservice.game_result.entity

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Document(collection = "game_info")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
data class GameInfo(

    @Id
    val id: UUID, // MongoDB의 `_id` 필드에 해당하며, UUID를 사용

    @Field("player_ids") // BSON 필드 이름을 명시적으로 지정
    val playerIds: List<Long>?, // JSON 타입을 List<String>으로 매핑

    @Field("ai_ids")
    val aiIds: List<Long>?, // JSON 타입을 List<String>으로 매핑

    @CreatedDate // 문서가 생성될 때 자동으로 날짜와 시간을 저장
    @Field("created_at")
    val createdAt: LocalDate?,

    @Field("game_type")
    val gameType: String?,

    @Field("winner_ai_id")
    val winnerAiId: Long?,
)
