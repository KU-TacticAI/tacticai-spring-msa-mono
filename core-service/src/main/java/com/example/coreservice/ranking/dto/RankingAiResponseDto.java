package com.example.coreservice.ranking.dto;

import com.example.commonmodule.enums.Tier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingAiResponseDto {

  private Long aiId;
  private String aiName;
  private String gameType;
  private Double winRate;
  private Tier tier;
}
