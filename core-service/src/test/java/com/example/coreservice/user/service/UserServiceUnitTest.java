package com.example.coreservice.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.commonmodule.dto.PagingDto;
import com.example.coreservice.user.dto.CreateUserRequestDto;
import com.example.coreservice.user.dto.UserResponseDto;
import com.example.coreservice.user.entity.Users;
import com.example.coreservice.user.repository.UserRepository;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class UserServiceUnitTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Test
  @DisplayName("유서 생성 성공")
  void createUser_success() {
    // Given
    CreateUserRequestDto testUserDto = CreateUserRequestDto.builder()
        .email("test@test.com")
        .password("testPassword")
        .username("testUsername")
        .nickname("testNickname")
        .build();
    // When
    UserResponseDto userResponseDto = userService.createUser(testUserDto);
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
    UserResponseDto userResponseDto = userService.findUsers(1L);
    // Then
    assertNotNull(userResponseDto);
    assertEquals("email@email.com", userResponseDto.getEmail());
  }
}