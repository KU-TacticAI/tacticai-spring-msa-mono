package com.example.coreservice.ai_statistics.controller;

import com.example.commonmodule.util.JWTUtil;
import com.example.coreservice.ai_statistics.dto.AiStatisticsDto;
import com.example.coreservice.ai_statistics.service.AiStatisticsService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai-statistics")
public class AiStatisticsController {

  private final AiStatisticsService aiStatisticsService;
  private final JWTUtil jwtUtil;

  @GetMapping
  public ResponseEntity<List<AiStatisticsDto>> getAiStatics(
      @RequestHeader("Authorization") String token
  ){
    String userId = jwtUtil.getUserId(token);
    return ResponseEntity.ok().body(aiStatisticsService.getAiStatics(userId));
  }
}
