package com.example.coreservice.ai.dto;

import com.example.coreservice.ai.entity.AiAgent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiResponseDto {

  private Long aiId;

  private Long userId;

  private String name;

  private String description;

  private String gameType;

  private String aiUrl;

  public static AiResponseDto toDto (AiAgent aiAgent) {
    return AiResponseDto.builder()
        .aiId(aiAgent.getId())
        .userId(aiAgent.getUserId())
        .name(aiAgent.getName())
        .description(aiAgent.getDescription())
        .gameType(aiAgent.getGameType())
        .aiUrl(aiAgent.getAiUrl())
        .build();
  }
}
