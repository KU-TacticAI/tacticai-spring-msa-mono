package com.example.coreservice.ai_statistics.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiStatisticsDto {

  private Long aiId;

  private int gameCount;

  private double avg_turns;

  private int avgResponseTimeMs;

  private double winRate;
}
