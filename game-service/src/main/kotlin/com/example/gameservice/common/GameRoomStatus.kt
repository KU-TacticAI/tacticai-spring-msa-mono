package com.example.gameservice.common

enum class GameRoomStatus {
    WAITING,   // 생성 직후
    MATCHED,   // 게임 시작 준비 완료
    EXPIRED    // 시간이 지나 만료됨
}