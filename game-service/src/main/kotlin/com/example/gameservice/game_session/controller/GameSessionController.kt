package com.example.gameservice.game_session.controller

import com.example.commonmodule.dto.GameResultResponseDto
import com.example.gameservice.game_session.dto.GameSessionResponseDto
import com.example.gameservice.game_session.service.GameSessionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rooms/{roomId}")
class GameSessionController(
    val gameSessionService: GameSessionService
) {

    // 특정 스냅샷 받아오기
    @GetMapping("/snapshot/{id}")
    fun getSnapShot(
        @PathVariable("roomId") roomId : Long,
        @PathVariable("id") id: Long,
    ) : ResponseEntity<GameSessionResponseDto> {
        return ResponseEntity.ok().body(gameSessionService.getSnapShot(roomId, id))
    }

    // 게임 서버에서 시스템으로 스냅샷 전송
    @PostMapping("/snapshot")
    fun receiveSnapShot(
        @PathVariable("roomId") roomId: Long,
    ): ResponseEntity<GameSessionResponseDto> {
        return ResponseEntity.ok().body(gameSessionService.receiveSnapShot(roomId))
    }

    // 게임 결과 조회
    @GetMapping("/result")
    fun getResult(
        @PathVariable("roomId") roomId: Long,
    ): ResponseEntity<GameResultResponseDto> {
        return ResponseEntity.ok().body(gameSessionService.showResult(roomId.toString()))
    }
}