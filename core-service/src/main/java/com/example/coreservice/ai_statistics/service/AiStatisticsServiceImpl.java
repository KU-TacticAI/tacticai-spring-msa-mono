package com.example.coreservice.ai_statistics.service;

import com.example.coreservice.ai.dto.AiResponseDto;
import com.example.coreservice.ai.entity.AiAgent;
import com.example.coreservice.ai.service.AiService;
import com.example.coreservice.ai_statistics.dto.AiStatisticsDto;
import com.example.coreservice.ai_statistics.entity.AiStatistics;
import com.example.coreservice.ai_statistics.repository.AiStatisticsRepository;
import com.example.coreservice.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiStatisticsServiceImpl implements AiStatisticsService{

  private final AiService aiService;
  private final AiStatisticsRepository aiStatisticsRepository;

  @Override
  public List<AiStatisticsDto> getAiStatics(String userId) {
    List<AiResponseDto> aiAgents = aiService.getAllAI(Long.parseLong(userId));
    List<Long> aiIds = aiAgents.stream().map(AiResponseDto::getAiId).toList();
    List<AiStatistics> aiStatistics = aiStatisticsRepository.findByAiIdIn(aiIds);

    return aiStatistics.stream().map(ai ->{
      return AiStatisticsDto.builder()
          .aiId(ai.getAiId())
          .avg_turns(ai.getAvg_turns())
          .avgResponseTimeMs(ai.getAvgResponseTimeMs())
          .winRate(ai.getWinRate())
          .gameCount(ai.getGameCount())
        .build();
    }).toList();
  }
}
