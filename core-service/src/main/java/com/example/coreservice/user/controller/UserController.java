package com.example.coreservice.user.controller;

import com.example.coreservice.user.dto.CreateUserRequestDto;
import com.example.coreservice.user.dto.DeleteUserRequestDto;
import com.example.coreservice.user.dto.UpdatePasswordRequestDto;
import com.example.coreservice.user.dto.UpdateUserRequestDto;
import com.example.coreservice.user.dto.UserResponseDto;
import com.example.coreservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // 회원 가입
  @PostMapping("/sign-in")
  public ResponseEntity<UserResponseDto> signUp(
      @Valid @RequestPart(value = "json") CreateUserRequestDto userSignInRequestDto,
      @RequestPart(value = "file", required = false) MultipartFile file
  ) {
    return ResponseEntity.ok().body(userService.createUser(userSignInRequestDto));
  }

  // 유저 프로필 조회
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> findUser(
      @PathVariable Long id
  ){
    return ResponseEntity.ok().body(userService.findUsers(id));
  }

  // 유저 수정
  @PutMapping("/{id}")
  public ResponseEntity<UserResponseDto> updateUser(
      @PathVariable Long id,
      @Valid @RequestPart(value = "json") UpdateUserRequestDto updateUserRequestDto,
      @RequestPart(required = false) MultipartFile file
  ){
    return ResponseEntity.ok().body(userService.updateUsers(id, updateUserRequestDto, file));
  }

  // 비밀번호 변경
  @PatchMapping("/{id}/password")
  public ResponseEntity<UserResponseDto> changePassword(
      @PathVariable Long id,
      @Valid @RequestBody UpdatePasswordRequestDto userUpdatePasswordRequestDto
  ){
    return ResponseEntity.ok().body(
        userService.updateUserPassword(id, userUpdatePasswordRequestDto));
  }

  //회원 탈퇴
  @DeleteMapping("/{id}")
  public String deleteUser(
      @PathVariable Long id,
      @Valid @RequestBody DeleteUserRequestDto deleteUserRequestDto
  ) {
    // 탈퇴 처리 메서드 호출
    return userService.deleteUsers(id, deleteUserRequestDto);
  }
}
