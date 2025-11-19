package com.example.gameservice.game_result.entity

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime
import java.util.*

@Document(collection = "game_info")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
data class GameInfo(

    @Id
    val id: String?, // MongoDB ObjectId를 문자열로 저장 (null이면 자동 생성)

    @Field("player_ids") // BSON 필드 이름을 명시적으로 지정
    val playerIds: List<Long>?, // AI가 아닌 플레이어 ID 리스트

    @Field("ai_ids")
    val aiIds: List<Long>?, // AI 플레이어 ID 리스트

    @CreatedDate // 문서가 생성될 때 자동으로 날짜와 시간을 저장
    @Field("created_at")
    val createdAt: LocalDateTime?, // LocalDate에서 LocalDateTime으로 변경

    @Field("game_type")
    val gameType: String?, // Enum을 문자열로 저장

    @Field("winner_ai_id")
    val winnerAiId: Long?, // 승리한 AI의 ID

    @Field("client_gameinfo_id")
    val clientGameInfoId: String? // 클라이언트에서 생성한 게임 ID (UUID 문자열)
)