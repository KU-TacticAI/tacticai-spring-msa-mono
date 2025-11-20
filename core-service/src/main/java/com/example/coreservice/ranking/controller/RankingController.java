package com.example.coreservice.ranking.controller;

import com.example.coreservice.ranking.dto.RankingResponseDto;
import com.example.coreservice.ranking.service.RankingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RankingController {

  private final RankingService rankingService;

  @GetMapping("/user/ranking")
  public ResponseEntity<List<RankingResponseDto>> getAiRanking(
      Pageable pageable
  ) {
    List<RankingResponseDto> rankings = rankingService.getRankings(pageable);
    return ResponseEntity.ok(rankings);
  }

}
