package com.example.commonmodule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerResultDto {

  private Long userId;
  private String nickname;

  public PlayerResultDto(Long userId, String nickname) {
    this.userId = userId;
    this.nickname = nickname;
  }
}