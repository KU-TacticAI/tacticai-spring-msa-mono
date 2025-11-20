package com.example.coreservice.ai_statistics.service;

import com.example.coreservice.ai_statistics.dto.AiStatisticsDto;
import com.example.coreservice.ai_statistics.entity.AiStatistics;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AiStatisticsService {
  AiStatisticsDto createAiStatics(Long id);
  List<AiStatisticsDto> getAiStatics(String userId);

  void delete(Long id);

  List<AiStatistics> findByAiIdIn(List<Long> aiIds);
}
