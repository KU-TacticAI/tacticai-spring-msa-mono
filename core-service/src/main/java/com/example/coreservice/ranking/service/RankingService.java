package com.example.coreservice.ranking.service;

import com.example.coreservice.ranking.dto.RankingResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RankingService {
  List<RankingResponseDto> getRankings(Long totalScore, Pageable pageable);
  void recalculateAllUserRanking();
}
