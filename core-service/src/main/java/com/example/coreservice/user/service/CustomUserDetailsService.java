package com.example.coreservice.user.service;

import com.example.coreservice.user.entity.CustomUserDetails;
import com.example.coreservice.user.entity.Users;
import com.example.coreservice.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Users user = userRepository.findByEmailOrElseThrow(username);
    if (user != null) {
      return new CustomUserDetails(user);
    }
    return null;
  }
}
