package com.example.coreservice.auth.controller;

import com.example.coreservice.auth.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

  private final TokenService tokenService;

  @PostMapping("/token/refresh")
  public void createNewToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    tokenService.createNewToken(request, response);
  }
}
