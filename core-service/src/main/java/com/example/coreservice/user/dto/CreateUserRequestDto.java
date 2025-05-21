package com.example.coreservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDto {

  private String email;

  private String password;

  private String username;

  private String nickname;
}
