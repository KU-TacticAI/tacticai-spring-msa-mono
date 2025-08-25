package com.example.commonmodule.dto;

import com.example.commonmodule.enums.AiStatus;
import com.example.commonmodule.enums.Tier;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiUrlsResponseDto {

  private Long aiId;

  private Long userId;

  private String name;

  private String gameType;

  private String description;

  private Long score;

  @Enumerated(EnumType.STRING)
  private Tier tier;

  @Enumerated(EnumType.STRING)
  private AiStatus status;

  private String aiUrl = null;

}
