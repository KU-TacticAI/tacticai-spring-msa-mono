package com.example.gameservice.gamelobby.controller

import com.example.gameservice.gamelobby.dto.CreateRoomRequestDto
import com.example.gameservice.gamelobby.dto.RoomResponseDto
import com.example.gameservice.gamelobby.service.LobbyService
import org.jetbrains.annotations.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rooms")
class LobbyController(
    private val lobbyService: LobbyService
) {

    // 대기방 생성
    @PostMapping
    fun createRoom(
        @RequestBody requestData: CreateRoomRequestDto
    ): ResponseEntity<RoomResponseDto> {
        return ResponseEntity.ok().body(lobbyService.createRoom(requestData))
    }

    // 대기방 다건 조회
    @GetMapping
    fun findAllRooms(): ResponseEntity<List<RoomResponseDto>> {
        return ResponseEntity.ok().body(lobbyService.findAllRooms())
    }

    // 대기방 입장
    @PostMapping("/{roomId}")
    fun enterRoom(
        @NotNull @PathVariable roomId: Long,
    ): ResponseEntity<String> {
        return ResponseEntity.ok().body(lobbyService.enterRoom(roomId))
    }

    // 대기방 단건 조회
    @GetMapping("/{roomId}")
    fun findRoom(@PathVariable roomId: Long): ResponseEntity<RoomResponseDto> {
        return ResponseEntity.ok().body(lobbyService.findRoomById(roomId))
    }

    // AI 모델 선택
    @PostMapping("/{roomId}/ai/{aiId}")
    fun selectAi(
        @PathVariable roomId: Long,
        @PathVariable aiId: Long,
    ): ResponseEntity<RoomResponseDto> {
        return ResponseEntity.ok().body(lobbyService.selectAi(roomId, aiId))
    }

    // 게임 시작 요청
    @PostMapping("/{roomId}/start")
    fun startRoom(
        @PathVariable roomId: Long,
    ): ResponseEntity<RoomResponseDto> {
        return ResponseEntity.ok().body(lobbyService.startRoom(roomId))
    }

    // 게임 서버로 AI + 게임 전송
    @PostMapping("/{roomId}/start/game-info")
    fun startGameInfo(
        @PathVariable roomId: Long,
    ): ResponseEntity<RoomResponseDto> {
        return ResponseEntity.ok().body(lobbyService.startGameInfo(roomId))
    }
}