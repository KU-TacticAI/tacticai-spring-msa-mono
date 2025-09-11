package com.example.coreservice.ai.dto;

import com.example.coreservice.ai.entity.AiAgent;
import java.time.LocalDate;
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

  private Long aiSize;

  private LocalDate uploadDate;

  private String score;

  private String tier;

  private LocalDate updateAt;

  private String version;

  public static AiResponseDto toDto (AiAgent aiAgent) {
    return AiResponseDto.builder()
        .aiId(aiAgent.getId())
        .userId(aiAgent.getUserId())
        .name(aiAgent.getName())
        .description(aiAgent.getDescription())
        .gameType(aiAgent.getGameType())
        .aiUrl(aiAgent.getAiUrl())
        .aiSize(aiAgent.getAiSize())
        .updateAt(aiAgent.getCreatedAt().toLocalDate())
        .score(aiAgent.getScore().toString())
        .tier(aiAgent.getTier().toString())
        .uploadDate(aiAgent.getUpdatedAt().toLocalDate())
        .version(aiAgent.getVersion())
        .build();
  }
}
