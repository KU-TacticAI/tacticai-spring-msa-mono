package com.example.coreservice.user.service;

import com.example.commonmodule.dto.PagingDto;
import com.example.coreservice.user.dto.CreateUserRequestDto;
import com.example.coreservice.user.dto.DeleteUserRequestDto;
import com.example.coreservice.user.dto.UpdatePasswordRequestDto;
import com.example.coreservice.user.dto.UpdateUserRequestDto;
import com.example.coreservice.user.dto.UserResponseDto;
import com.example.coreservice.user.entity.Users;
import com.example.coreservice.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  // 유저 생성
  public UserResponseDto createUser(CreateUserRequestDto createUserDto) {
    if(userRepository.existsByEmail(createUserDto.getEmail())){
//      throw
    }
    Users user = Users.builder()
        .email(createUserDto.getEmail())
        .password(bCryptPasswordEncoder.encode(createUserDto.getPassword()))
        .username(createUserDto.getUsername())
        .nickname(createUserDto.getNickname())
        .build();
    userRepository.save(user);
    return UserResponseDto.toDto(user);
  }

  // 유저 단일 조회
  public UserResponseDto findUsers(Long userId) {
    Users user = userRepository.findByIdOrElseThrow(userId);
    return UserResponseDto.toDto(user);
  }

  // 유저 업데이트
  @Transactional
  public UserResponseDto updateUsers(Long userId, UpdateUserRequestDto updateUserRequestDto, MultipartFile file){
    Users user = checkUserPassword(userId, updateUserRequestDto.getPassword());
    user.updateNickname(updateUserRequestDto.getNickName());
    userRepository.save(user);
    return UserResponseDto.toDto(user);
  }

  // 비밀번호 수정
  @Transactional
  public UserResponseDto updateUserPassword(Long userId, UpdatePasswordRequestDto updatePasswordRequestDto){
    Users user = checkUserPassword(userId, updatePasswordRequestDto.getOldPassword());
    user.updatePassword(bCryptPasswordEncoder.encode(updatePasswordRequestDto.getNewPassword()));
    userRepository.save(user);
    return UserResponseDto.toDto(user);
  }

  // 유저 삭제
  @Transactional
  public String deleteUsers(Long userId, DeleteUserRequestDto deleteUserRequestDto) {
    Users user =checkUserPassword(userId, deleteUserRequestDto.getPassword());
    userRepository.delete(user);
    return "삭제되었습니다.";
  }

  private Users checkUserPassword(Long userId, String password) {
    Users user = userRepository.findByIdOrElseThrow(userId);
    if(user.getPassword().equals(bCryptPasswordEncoder.encode(password))){
//      throw
    }
    return user;
  }
}
