package com.example.coreservice.ranking.service;

import com.example.coreservice.ranking.dto.RankingResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RankingService {
  List<RankingResponseDto> getRankings(Pageable pageable);
  void recalculateAllUserRanking();
}
