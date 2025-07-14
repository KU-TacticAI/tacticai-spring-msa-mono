package com.example.commonmodule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerResultDto {

  private Long userId;
  private String nickname;
  private int turns;
  private double avgResponseTime;
  private int score;
  private String tier;

  public PlayerResultDto(Long userId, String nickname, int turns, double avgResponseTime, int score, String tier) {
    this.userId = userId;
    this.nickname = nickname;
    this.turns = turns;
    this.avgResponseTime = avgResponseTime;
    this.score = score;
    this.tier = tier;
  }
}