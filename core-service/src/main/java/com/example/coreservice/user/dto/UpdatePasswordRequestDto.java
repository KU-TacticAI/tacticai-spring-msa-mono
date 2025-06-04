package com.example.coreservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordRequestDto {
  private String oldPassword;
  private String newPassword;
}
