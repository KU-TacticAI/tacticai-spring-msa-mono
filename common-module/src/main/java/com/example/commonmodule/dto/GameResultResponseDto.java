package com.example.commonmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResultResponseDto {

  private Long roomId;
  private PlayerResultDto winner;
  private PlayerResultDto loser;
}
