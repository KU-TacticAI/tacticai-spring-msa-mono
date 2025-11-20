package com.example.coreservice.user.service;

import com.example.commonmodule.dto.PlayerResultDto;
import com.example.commonmodule.exceptions.DuplicatedException;
import com.example.commonmodule.exceptions.InvalidInputException;
import com.example.commonmodule.s3.service.S3Service;
import com.example.coreservice.enums.Role;
import com.example.coreservice.exceptions.UserException;
import com.example.coreservice.user.dto.CreateUserRequestDto;
import com.example.coreservice.user.dto.DeleteUserRequestDto;
import com.example.coreservice.user.dto.UpdatePasswordRequestDto;
import com.example.coreservice.user.dto.UpdateUserRequestDto;
import com.example.coreservice.user.dto.UserResponseDto;
import com.example.coreservice.user.entity.Users;
import com.example.coreservice.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

  private final UserRepository userRepository;
  private final S3Service s3Service;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  // 유저 생성
  public UserResponseDto createUser(CreateUserRequestDto createUserDto, MultipartFile file) {
    if(userRepository.existsByEmail(createUserDto.getEmail())){
      throw new DuplicatedException(UserException.EMAIL_EXIST);
    }

    String profileLink = "";
    if (file != null) {
      profileLink = s3Service.uploadFile(file).getFilePath();
    }

    Users user = Users.builder()
        .email(createUserDto.getEmail())
        .password(bCryptPasswordEncoder.encode(createUserDto.getPassword()))
        .username(createUserDto.getUsername())
        .nickname(createUserDto.getNickname())
        .profileLink(profileLink)
        .role(Role.USER)
        .ranking(-1L)
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
    if (file != null){
      if(!user.getProfileLink().isEmpty()){
        s3Service.deleteFile(user.getProfileLink());
      }
      user.updateProfileLink(s3Service.uploadFile(file).getFilePath());
    }
    user.updateNickname(updateUserRequestDto.getNickname());
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
    user.delete();
    userRepository.save(user);
    return "삭제되었습니다.";
  }

  public Users checkUserPassword(Long userId, String password) {
    Users user = userRepository.findByIdOrElseThrow(userId);
    if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
      throw new InvalidInputException(UserException.WRONG_PASSWORD);
    }
    return user;
  }

  @Override
  public List<Users> findUsers(List<Long> userIdList) {
    return userRepository.findByIdIn(userIdList);
  }

  @Override
  public PlayerResultDto findWinner(Long userId) {
    Users user = userRepository.findByIdOrElseThrow(userId);
    return PlayerResultDto.builder()
        .userId(user.getId())
        .nickname(user.getNickname())
        .ranking(user.getRanking())
        .build();
  }

  @Override
  public List<Users> findAllUsers() {
    return userRepository.findAllByDeletedAtIsNull();
  }

  @Override
  public void saveAll(List<Users> allUsers) {
    userRepository.saveAll(allUsers);
  }
}
