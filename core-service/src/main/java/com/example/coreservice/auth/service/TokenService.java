package com.example.coreservice.auth.service;

import com.example.commonmodule.common.TokenSettings;
import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.exceptions.TokenErrorCode;
import com.example.commonmodule.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final JWTUtil jwtUtil;

  public String[] createNewToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String refresh = extractRefreshToken(request);
    validateRefreshToken(refresh);

    String userId = jwtUtil.getUserId(refresh);
    String auth= jwtUtil.getAuth(refresh);

    if (!jwtUtil.checkRefreshTokenMatch(userId, refresh)) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    String redisKey = userId + auth;
    jwtUtil.deleteRefreshTokenFromRedis(redisKey);
    Cookie cookie = new Cookie(TokenSettings.REFRESH_TOKEN_CATEGORY, null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);

    // JWT 토큰 생성
    String[] tokens = jwtUtil.generateTokens(userId, auth);
    String accessToken = tokens[0];
    String refreshToken = tokens[1];
    Map<String, String> body = new HashMap<>();
    body.put("token", accessToken);
    // refresh token 쿠키 설정
    response.addCookie(jwtUtil.createCookie(TokenSettings.REFRESH_TOKEN_CATEGORY, refreshToken));

    response.setStatus(HttpServletResponse.SC_OK); // 302 Found 설정
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(
        "{\"token\" : \"" + accessToken + "\"}"
    );

    return tokens;
  }

  // HTTP 요청에서 Refresh 토큰 추출
  private String extractRefreshToken(HttpServletRequest request) {
    for (Cookie cookie : request.getCookies()) {
      if (cookie.getName().equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
        return cookie.getValue();
      }
    }
    throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
  }

  // Refresh 토큰 유효성 검사
  private void validateRefreshToken(String refresh) {
    try {
      jwtUtil.isExpired(refresh);
    } catch (ExpiredJwtException e) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }
    if (!jwtUtil.getCategory(refresh).equals(TokenSettings.REFRESH_TOKEN_CATEGORY)) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }
  }
}
