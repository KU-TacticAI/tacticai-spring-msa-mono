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

  private String username;

  private String nickname;

  private String profileLink;

  public static UserResponseDto toDto(Users user) {
    return UserResponseDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .username(user.getUsername())
        .nickname(user.getNickname())
        .profileLink(user.getProfileLink())
        .build();
  }
}
