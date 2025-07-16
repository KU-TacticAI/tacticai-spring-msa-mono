package com.example.coreservice.filter;


import com.example.commonmodule.common.TokenSettings;
import com.example.commonmodule.util.JWTUtil;
import com.example.coreservice.user.entity.CustomUserDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/**
 * CustomLoginFilter: 사용자 로그인 시 JWT 기반 인증을 수행하는 필터
 *
 * <p>- 사용자가 로그인하면 이메일과 비밀번호를 검증</p>
 * <p>- JWT 액세스 및 리프레시 토큰을 생성하여 클라이언트에게 전달</p>
 * <p>- Redis에 리프레시 토큰을 저장하여 보안 강화</p>
 */
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JWTUtil jwtUtil;

  /**
   * 사용자 로그인 요청을 처리하는 메서드
   * <p>- HTTP 요청에서 이메일 및 비밀번호를 추출하여 인증 요청</p>
   *
   * @param request  HTTP 요청 객체
   * @param response HTTP 응답 객체
   * @return 인증 정보 (Authentication)
   * @throws AuthenticationException 인증 실패 시 예외 발생
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, String> loginData = objectMapper.readValue(
          request.getInputStream(),
          new TypeReference<Map<String, String>>() {}
      );
      String email = loginData.get("email");
      String password = loginData.get("password");

      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(email, password, null));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 인증 성공 시 실행되는 메서드
   * <p>- JWT 액세스 및 리프레시 토큰을 생성하여 응답 헤더 및 쿠키에 저장</p>
   * <p>- Redis에 리프레시 토큰 저장</p>
   *
   * @param request        HTTP 요청 객체
   * @param response       HTTP 응답 객체
   * @param chain          필터 체인
   * @param authentication 인증 정보 (사용자 ID 및 권한 포함)
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication)
      throws IOException {
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = customUserDetails.getUserId().toString();
    String role = authentication.getAuthorities().iterator().next().getAuthority();

    // JWT 토큰 생성
    String[] tokens = jwtUtil.generateTokens(userId, role);
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
  }
}
