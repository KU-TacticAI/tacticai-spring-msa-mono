package com.example.gameservice.gamelobby.dto

data class PlayerSummaryDto(
    val userId: Long,
    val nickname: String?,      // 없으면 null
    val profileUrl: String?,     // 선택
    val ranking: Long?,          // 선택 (hostRanking 등)
    val ready: Boolean,         // 레디 상태
    val joinOrder: Int?,           // 좌석 번호가 있으면
)
