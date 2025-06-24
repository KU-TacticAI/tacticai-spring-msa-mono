package com.example.coreservice.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.coreservice.user.dto.CreateUserRequestDto;
import com.example.coreservice.user.dto.UserResponseDto;
import com.example.coreservice.user.entity.Users;
import com.example.coreservice.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

  @InjectMocks
  private UserServiceImpl userServiceImpl;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;


  @Test
  @DisplayName("유저 생성 성공")
  void createUser_success() {
    // Given
    CreateUserRequestDto testUserDto = CreateUserRequestDto.builder()
        .email("test@test.com")
        .password("testPassword")
        .username("testUsername")
        .nickname("testNickname")
        .build();
    // When
    UserResponseDto userResponseDto = userServiceImpl.createUser(testUserDto);
    // Then
    assertNotNull(userResponseDto);
    assertEquals("test@test.com", userResponseDto.getEmail());
  }

  @Test
  @DisplayName("단일 유저 조회 성공")
  void findUsers_success() {
    // Given
    Users mockUser = Users.builder()
        .id(1L)
        .email("email@email.com")
        .username("username")
        .nickname("nickname")
        .build();
    when(userRepository.findByIdOrElseThrow(any())).thenReturn(mockUser);
    // When
    UserResponseDto userResponseDto = userServiceImpl.findUsers(1L);
    // Then
    assertNotNull(userResponseDto);
    assertEquals("email@email.com", userResponseDto.getEmail());
  }
}