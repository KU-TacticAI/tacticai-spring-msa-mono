package com.example.coreservice.user.service;

import com.example.commonmodule.dto.PlayerResultDto;
import com.example.coreservice.user.dto.CreateUserRequestDto;
import com.example.coreservice.user.dto.DeleteUserRequestDto;
import com.example.coreservice.user.dto.UpdatePasswordRequestDto;
import com.example.coreservice.user.dto.UpdateUserRequestDto;
import com.example.coreservice.user.dto.UserResponseDto;
import com.example.coreservice.user.entity.Users;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public interface UserService {
  UserResponseDto createUser(CreateUserRequestDto createUserDto);
  UserResponseDto findUsers(Long userId);
  UserResponseDto updateUsers(Long userId, UpdateUserRequestDto updateUserRequestDto, MultipartFile file);
  UserResponseDto updateUserPassword(Long userId, UpdatePasswordRequestDto updatePasswordRequestDto);
  String deleteUsers(Long userId, DeleteUserRequestDto deleteUserRequestDto);

  Users checkUserPassword(Long userId, String password);
  List<Users> findUsers(List<Long> userIdList);
  PlayerResultDto findWinner(Long userId);
}
