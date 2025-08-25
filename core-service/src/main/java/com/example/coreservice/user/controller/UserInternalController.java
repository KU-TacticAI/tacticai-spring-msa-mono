package com.example.coreservice.user.controller;

import com.example.commonmodule.dto.PlayerResultDto;
import com.example.coreservice.user.entity.Users;
import com.example.coreservice.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/list")
  public ResponseEntity<List<PlayerResultDto>> getAllUsers(
      @RequestParam("ids") List<Long> userIds
  ) {
    List<Users> users = userService.findUsers(userIds);
    List<PlayerResultDto> playerResultDtos = users.stream().map( u ->  {
    return PlayerResultDto.builder()
        .userId(u.getId())
        .ranking(u.getRanking())
        .nickname(u.getNickname())
        .profileLink(u.getProfileLink())
        .build();
    }).toList();
    return ResponseEntity.ok(playerResultDtos);
  }
}