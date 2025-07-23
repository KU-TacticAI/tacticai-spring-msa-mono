package com.example.coreservice.user.controller;

import com.example.commonmodule.dto.PlayerResultDto;
import com.example.coreservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

  private final UserService userService;

  @GetMapping("/{id}")
  public ResponseEntity<PlayerResultDto> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.findWinner(id));
  }
}