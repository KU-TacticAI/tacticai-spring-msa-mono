package com.example.coreservice.user.service;

import com.example.coreservice.user.dto.CreateUserRequestDto;
import com.example.coreservice.user.dto.UserResponseDto;
import com.example.coreservice.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public UserResponseDto createUser(CreateUserRequestDto createUserDto) {
    return null;
  }

}
