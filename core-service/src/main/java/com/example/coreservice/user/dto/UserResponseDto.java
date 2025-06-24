package com.example.coreservice.user.dto;

import com.example.coreservice.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

  private Long id;

  private String email;

  private String nickname;

  public static UserResponseDto toDto(Users user) {
    return UserResponseDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .build();
  }
}
