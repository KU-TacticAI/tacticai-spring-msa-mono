package com.example.coreservice.auth.service;

import com.example.commonmodule.common.TokenSettings;
import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.exceptions.TokenErrorCode;
import com.example.commonmodule.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final JWTUtil jwtUtil;

  public String[] createNewToken(HttpServletRequest request) {
    String refresh = extractRefreshToken(request);
    validateRefreshToken(refresh);

    String userId = jwtUtil.getUserId(refresh);
    String auth = jwtUtil.getAuth(refresh);

    if (!jwtUtil.checkRefreshTokenMatch(userId, refresh)) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    // 새로운 액세스 & 리프레시 토큰 생성
    return jwtUtil.generateTokens(userId, auth);
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
