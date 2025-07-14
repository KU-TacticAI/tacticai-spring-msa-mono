package com.example.commonmodule.util;

import com.example.commonmodule.common.TokenSettings;
import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.exceptions.TokenErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

  private final RedisTemplate<String, String> redisTemplate;
  private SecretKey secretKey;

  public JWTUtil(@Qualifier("redisTemplate")RedisTemplate<String, String> redisTemplate,
      @Value("${spring.jwt.secret}") String secret) {
    this.redisTemplate = redisTemplate;
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  // 헤더에서 유저 아이디를 가져오는 메소드 ( Long 타입 )
  public Long findUserByToken(String authorizationHeader) {
    String token = authorizationHeader.replace(TokenSettings.TOKEN_TYPE, "").trim();
    return Long.parseLong(this.getUserId(token));
  }

  // 헤더에서 유저 권한을 가져오는 메소드 ( String 타입 )
  public String findAuthByToken(String authorizationHeader) {
    String token = authorizationHeader.replace(TokenSettings.TOKEN_TYPE, "").trim();
    return this.getAuth(token);
  }

  // 토큰에서 유저 아이디를 가져오는 메소드 ( String 타입 )
  public String getUserId(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get("userId", String.class);
  }

  // Access 토큰 / Refresh 토큰 구분
  public String getCategory(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get("category", String.class);
  }

  // 토큰의 권한을 반환
  public String getAuth(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .get("role", String.class);
  }

  // 발급자를 확인하는 메소드
  public boolean isIssuer(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .getIssuer().equals(TokenSettings.TOKEN_ISSUER);
  }

  // 만료 확인하는 메소드
  public Boolean isExpired(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
        .getExpiration().before(new Date());
  }

  /**
   * 사용자 ID와 권한을 이용하여 Access Token 및 Refresh Token 생성
   *
   * @param userId 사용자 ID
   * @param role 사용자 권한
   * @return [0] -> 액세스 토큰, [1] -> 리프레시 토큰
   */
  public String[] generateTokens(String userId, String role) {
    String access = createJwt(TokenSettings.ACCESS_TOKEN_CATEGORY, userId, role, TokenSettings.ACCESS_TOKEN_EXPIRATION);
    String refresh = createJwt(TokenSettings.REFRESH_TOKEN_CATEGORY, TokenSettings.REFRESH_TOKEN_CATEGORY + userId, role, TokenSettings.REFRESH_TOKEN_EXPIRATION);

//    storeRefreshToken(TokenSettings.REFRESH_TOKEN_CATEGORY + userId, refresh);

    return new String[]{access, refresh};
  }

  // 토큰을 생성하는 메소드
  public String createJwt(String category, String userId, String role, Long expiredMs) {

    return Jwts.builder()
        .issuer(TokenSettings.TOKEN_ISSUER) // 발급자 설정
        .claim("category", category)  // 토큰 카테고리 ( access / refresh )
        .claim("userId", userId)  // 토큰에 있는 유저 Id
        .claim("role", role)      // 유저의 권한
        .issuedAt(new Date(System.currentTimeMillis())) // 토큰 생성 일자
        .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 토큰 만료시간
        .signWith(secretKey)  // secretKey를 통한 토큰 암호화
        .compact();
  }

  /**
   * Redis에 Refresh Token을 저장
   *
   * @param userId 사용자 ID
   * @param refreshToken 저장할 리프레시 토큰
   */
  public void storeRefreshToken(String userId, String refreshToken) {
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set(userId, refreshToken, Duration.ofMillis(TokenSettings.REFRESH_TOKEN_EXPIRATION));
  }

  /**
   * Redis에서 Refresh Token을 가져오는 메서드
   *
   * @param userId 사용자 ID
   * @return 저장된 Refresh Token
   */
  public String fetchRefreshTokenFromRedis(String userId) {
    return redisTemplate.opsForValue().get(userId);
  }

  /**
   * Redis에서 Refresh Token 삭제 (로그아웃 시 사용)
   */
  public void deleteRefreshTokenFromRedis(String userId) {
    redisTemplate.delete(userId);
  }

  /**
   * Refresh Token의 유효성 검증
   *
   * @param refreshToken 클라이언트가 제공한 Refresh Token
   */
  public void verifyRefreshTokenFormat(String refreshToken) {
    try {
      isExpired(refreshToken);
      isIssuer(refreshToken);
    } catch (ExpiredJwtException e) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }

    if (!TokenSettings.REFRESH_TOKEN_CATEGORY.equals(getCategory(refreshToken))) {
      throw new NoAuthorizedException(TokenErrorCode.NO_REFRESH_TOKEN);
    }
  }

  /**
   * Redis에 저장된 Refresh Token과 요청된 Refresh Token을 비교하여 검증
   *
   * @param userId 사용자 ID
   * @param refreshToken 요청된 Refresh Token
   */
  public boolean checkRefreshTokenMatch(String userId, String refreshToken) {
    String storedToken = redisTemplate.opsForValue().get(userId);
    return storedToken != null && storedToken.equals(refreshToken);
  }

  /**
   * Refresh Token을 가져오는 메서드 (Redis에서 가져오기)
   *
   * @param userId 사용자 ID
   * @return 저장된 Refresh Token
   */
  public String getStoredRefreshToken(String userId) {
    return redisTemplate.opsForValue().get(userId);
  }

  // 쿠키에 리플레시 토큰을 담기위해 쿠키를 생성하는 로직
//  public Cookie createCookie(String key, String value) {
//    Cookie cookie = new Cookie(key, value);
//    // 쿠키 1일 유지
//    cookie.setMaxAge(TokenSettings.COOKIE_EXPIRATION);
//    //cookie.setSecure(true);
//    cookie.setPath("/");
//    cookie.setHttpOnly(true);
//
//    return cookie;
//  }
  public ResponseCookie createCookie(String key, String value) {
    return ResponseCookie.from(key, value)
        .httpOnly(true)
        .path("/")
        .maxAge(Duration.ofDays(1))
        .build();
  }
}
