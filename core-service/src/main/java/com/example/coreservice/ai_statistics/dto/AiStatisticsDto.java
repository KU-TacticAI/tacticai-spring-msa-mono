package com.example.coreservice.ai_statistics.dto;

import com.example.coreservice.ai_statistics.entity.AiStatistics;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiStatisticsDto {

  private Long aiId;

  private int gameCount;

  private double avgTurns;

  private int avgResponseTimeMs;

  private double winRate;

  public static AiStatisticsDto toDto(AiStatistics aiStatistics){
    return AiStatisticsDto.builder()
        .aiId(aiStatistics.getAiId())
        .gameCount(aiStatistics.getGameCount())
        .avgTurns(aiStatistics.getAvgTurns())
        .avgResponseTimeMs(aiStatistics.getAvgResponseTimeMs())
        .winRate(aiStatistics.getWinRate())
        .build();

  }
}
