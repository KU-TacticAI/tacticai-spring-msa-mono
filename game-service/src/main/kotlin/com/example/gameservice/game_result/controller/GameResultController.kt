package com.example.gameservice.game_result.controller

import com.example.commonmodule.util.JWTUtil
import com.example.gameservice.game_result.dto.GameResultResponseDto
import com.example.gameservice.game_result.service.GameResultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/result")
class GameResultController(
    val gameResultService: GameResultService,
    val jwtUtil: JWTUtil,
) {

    @GetMapping
    fun getGameResult(
        @RequestHeader("Authorization") token:String,
    ):ResponseEntity<List<GameResultResponseDto>>{
        val userId :String = jwtUtil.getUserId(token);
        return ResponseEntity.ok().body(gameResultService.getGameResultByUserId(userId.toLong()));
    }

}