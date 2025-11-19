package com.example.gameservice.game_result.entity

import com.example.gameservice.common.GameStatus
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "game_detail_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
data class GameDetailLog(

    @Id
    @Indexed
    val id: String?, // MongoDB ObjectId를 문자열로 저장 (null이면 자동 생성)

    @Field("response_time_ms")
    val responseTimeMs: Int?, // AI 응답 시간 (밀리초)

    @Field("board_snapshot")
    val boardSnapshot: Map<String, Any>?, // 해당 턴의 보드 상태

    @Field("turn_count")
    val turnCount: Int?, // 턴 번호

    @Field("move_data")
    val moveData: String?, // 실제 수 데이터 (좌표 등)

    @Field("status")
    val status: GameStatus?, // 게임 진행 상태

    @Field("log_output")
    val logOutput: Map<String, Any>?, // 로그 메시지 또는 추가 정보

    @Field("ai_id")
    val aiId: Long?, // 해당 턴을 플레이한 AI의 ID

    @Field("gameinfo_id")
    val gameInfoId: String? // game_info 컬렉션의 _id 참조 (문자열)
)