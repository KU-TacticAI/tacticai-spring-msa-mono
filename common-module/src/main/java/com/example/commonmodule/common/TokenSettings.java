package com.example.commonmodule.common;

import lombok.Getter;

@Getter
public final class TokenSettings {

  private TokenSettings(){
  }

  // 토큰 만료 시간 (밀리초 단위)
//  public static final long ACCESS_TOKEN_EXPIRATION = 10 * 60 * 10000000000000L; // 10분
  public static final long ACCESS_TOKEN_EXPIRATION = 10 * 60 * 1000L * 1234567890; // 10분
  public static final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L; // 24시간

  // 토큰 공통 속성
  public static final String TOKEN_ISSUER = "taticalAi"; // 토큰 발급자
  public static final String TOKEN_TYPE = "Bearer "; // 토큰 타입
  public static final String ACCESS_TOKEN_CATEGORY = "Authorization"; // 액세스 토큰 카테고리
  public static final String REFRESH_TOKEN_CATEGORY = "refresh"; // 리프레시 토큰 카테고리

  // 쿠키 만료 시간
  public static final int COOKIE_EXPIRATION = 24 * 60 * 60;
}
