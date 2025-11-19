package com.example.coreservice.ai_statistics.service;

import com.example.coreservice.ai.dto.AiResponseDto;
import com.example.coreservice.ai.entity.AiAgent;
import com.example.coreservice.ai.repository.AiRepository;
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

//  private final AiService aiService;
  private final AiRepository aiRepository;
  private final AiStatisticsRepository aiStatisticsRepository;

  @Override
  public AiStatisticsDto createAiStatics(Long id) {
    AiStatistics aiStatistics = AiStatistics.builder()
        .aiId(id)
        .draws(0)
        .wins(0)
        .losses(0)
        .winRate(0)
        .avgTurns(0)
        .avgResponseTimeMs(0)
        .gameCount(0)
        .deletedAt(null)
        .build();
    AiStatistics savedAiStatistics = aiStatisticsRepository.save(aiStatistics);
    return AiStatisticsDto.toDto(savedAiStatistics);
  }

  @Override
  public List<AiStatisticsDto> getAiStatics(String userId) {
//    List<AiResponseDto> aiAgents = aiService.getAllAI(Long.parseLong(userId));
    List<AiAgent> aiAgents = aiRepository.getAiAgentsByUserId(Long.parseLong(userId));
    List<Long> aiIds = aiAgents.stream().map(AiAgent::getId).toList();
    List<AiStatistics> aiStatistics = aiStatisticsRepository.findByAiIdIn(aiIds);

    return aiStatistics.stream().map(ai ->{
      return AiStatisticsDto.builder()
          .aiId(ai.getAiId())
          .avgTurns(ai.getAvgTurns())
          .avgResponseTimeMs(ai.getAvgResponseTimeMs())
          .winRate(ai.getWinRate())
          .gameCount(ai.getGameCount())
        .build();
    }).toList();
  }

  @Override
  public void delete(Long id) {
    AiStatistics aiStatistics = aiStatisticsRepository.findById(id).orElseThrow();
    aiStatistics.delete();
    aiStatisticsRepository.save(aiStatistics);
  }
}
