package com.example.gameservice.gamelobby.controller

import com.example.commonmodule.util.JWTUtil
import com.example.gameservice.gamelobby.dto.CreateRoomRequestDto
import com.example.gameservice.gamelobby.dto.ResponseLobbyDto
import com.example.gameservice.gamelobby.dto.RoomResponseDto
import com.example.gameservice.gamelobby.service.LobbyService
import org.jetbrains.annotations.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rooms")
class LobbyController(
    private val lobbyService: LobbyService,
    private val jwtUtil: JWTUtil
) {

    // 대기방 생성
    @PostMapping
    fun createRoom(
        @RequestHeader("Authorization") token: String,
        @RequestBody requestData: CreateRoomRequestDto
    ): ResponseEntity<RoomResponseDto> {
        val userId = jwtUtil.getUserId(token)
        return ResponseEntity.ok().body(lobbyService.createRoom(userId, requestData))
    }

    // 대기방 다건 조회
    @GetMapping("/lobby/{gameName}")
    fun findAllRooms(
        @PathVariable gameName: String,
    ): ResponseEntity<List<RoomResponseDto>> {
        return ResponseEntity.ok().body(lobbyService.findAllRooms(gameName))
    }

    // 대기방 입장
    @PostMapping("/{roomId}")
    fun enterRoom(
        @NotNull @PathVariable roomId: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<String> {
        val userId = jwtUtil.getUserId(token).toLong()
        return ResponseEntity.ok().body(lobbyService.enterRoom(roomId, userId))
    }

    // 대기방 단건 조회
    @GetMapping("/{roomId}")
    fun findRoom(@PathVariable roomId: Long): ResponseEntity<RoomResponseDto> {
        return ResponseEntity.ok().body(lobbyService.findRoomById(roomId))
    }

    // AI 모델 선택
    @PostMapping("/{roomId}/ai/{aiId}")
    fun selectAi(
        @RequestHeader("Authorization") token: String,
        @PathVariable roomId: Long,
        @PathVariable aiId: Long,
    ): ResponseEntity<ResponseLobbyDto> {
        val userId = jwtUtil.getUserId(token).toLong()
        return ResponseEntity.ok().body(lobbyService.selectAi(roomId, userId, aiId))
    }

    // 게임 시작 요청
    @PostMapping("/{roomId}/start")
    fun startRoom(
        @PathVariable roomId: Long,
    ): ResponseEntity<RoomResponseDto> {
        return ResponseEntity.ok().body(lobbyService.startRoom(roomId))
    }
}