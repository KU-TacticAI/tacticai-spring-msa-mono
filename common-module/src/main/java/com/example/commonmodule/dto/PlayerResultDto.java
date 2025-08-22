package com.example.commonmodule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerResultDto {

  private Long userId;
  private String nickname;
  private Long ranking;
  private String profileLink;

  public PlayerResultDto(Long userId, String nickname, Long ranking, String profileLink) {
    this.userId = userId;
    this.nickname = nickname;
    this.ranking = ranking;
    this.profileLink = profileLink;
  }
}