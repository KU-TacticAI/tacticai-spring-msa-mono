package com.example.commonmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

  private Long userId;
  private String nickname;
  private String email;
  private String profileImage;
  private String tier;
}
