//package com.example.coreservice.filter;
//
//import com.example.commonmodule.common.TokenSettings;
//import com.example.commonmodule.util.JWTUtil;
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.filter.GenericFilterBean;
//
///**
// * CustomLogoutFilter: JWT 기반 로그아웃을 처리하는 필터
// *
// * <p>- 로그아웃 요청을 감지하고, Refresh Token을 검증하여 삭제</p>
// * <p>- Redis에 저장된 Refresh Token 제거</p>
// * <p>- 쿠키에서 Refresh Token을 가져와 검증 후 폐기</p>
// * <p>- 유효하지 않은 요청은 400(Bad Request) 반환</p>
// */
//@RequiredArgsConstructor
//public class CustomLogoutFilter extends GenericFilterBean {
//
//  private final JWTUtil jwtUtil;
//
//  /**
//   * 모든 요청에서 로그아웃을 확인하는 메서드
//   *
//   * @param request  HTTP 요청 객체
//   * @param response HTTP 응답 객체
//   * @param chain    필터 체인 객체
//   * @throws ServletException, IOException
//   */
//  @Override
//  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//      throws IOException, ServletException {
//    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
//      doLogoutFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
//    } else {
//      chain.doFilter(request, response);
//    }
//  }
//
//  /**
//   * 로그아웃 요청인지 확인하고 처리하는 메서드
//   *
//   * @param request     HTTP 요청 객체
//   * @param response    HTTP 응답 객체
//   * @param filterChain 필터 체인 객체
//   * @throws IOException, ServletException
//   */
//  private void doLogoutFilter(HttpServletRequest request, HttpServletResponse response,
//      FilterChain filterChain) throws IOException, ServletException {
//    if (!isLogoutRequest(request)) {
//      filterChain.doFilter(request, response);
//      return;
//    }
//
//    String refreshToken = extractRefreshToken(request);
//    if (refreshToken == null) {
//      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//      return;
//    }
//
//    if (!validateToken(refreshToken, response)) {
//      return;
//    }
//    performLogout(refreshToken, response);
//  }
//
//  /**
//   * 로그아웃 요청인지 / POST 요청인지 확인하는 메서드
//   *
//   * @param request
//   * @return ture 로그아웃 / POST 요청<p></p> false 로그아웃이 아니거나 POST 요청이 아닐 시
//   */
//  private boolean isLogoutRequest(HttpServletRequest request) {
//    return "/logout".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(
//        request.getMethod());
//  }
//
//  /**
//   * 쿠키에서 refresh token을 추출하는 메서드
//   *
//   * @param request HTTP 요청 객체
//   * @return refresh token, 없으면 null
//   */
//  private String extractRefreshToken(HttpServletRequest request) {
//    Cookie[] cookies = request.getCookies();
//    if (cookies == null) {
//      return null;
//    }
//
//    for (Cookie cookie : cookies) {
//      if (TokenSettings.REFRESH_TOKEN_CATEGORY.equals(cookie.getName())) {
//        return cookie.getValue();
//      }
//    }
//    return null;
//  }
//
//  /**
//   * 토큰의 유효성 검사 발급자, 만료기간 검사
//   *
//   * @param token    발급된 토큰
//   * @param response HTTP 응답 객체
//   * @return true 정상적인 토큰,<p></p> false 만료 혹은 유효하지 않은 발급자
//   * @throws IOException
//   */
//  private boolean validateToken(String token, HttpServletResponse response) throws IOException {
//    try {
//      jwtUtil.isExpired(token);
//      jwtUtil.isIssuer(token);
//    } catch (ExpiredJwtException e) {
//      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//      return false;
//    }
//
//    if (!TokenSettings.REFRESH_TOKEN_CATEGORY.equals(jwtUtil.getCategory(token))) {
//      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//      return false;
//    }
//    return true;
//  }
//
//  /**
//   * 저장된 refresh token을 삭제하는 메서드
//   *
//   * @param refreshToken 저장된 refresh token
//   * @param response     HTTP 응답 객체
//   */
//  private void performLogout(String refreshToken, HttpServletResponse response) {
//    String userId = jwtUtil.getUserId(refreshToken);
//
//    if (!jwtUtil.checkRefreshTokenMatch(userId, refreshToken)) {
//      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//      return;
//    }
//
//    jwtUtil.deleteRefreshTokenFromRedis(userId);
//
//    clearCookies(response);
//    response.setStatus(HttpServletResponse.SC_OK);
//  }
//
//  /**
//   * 쿠키를 삭제하는 메서드
//   *
//   * @param response HTTP 응답 객체
//   */
//  private void clearCookies(HttpServletResponse response) {
//    Cookie cookie = new Cookie(TokenSettings.REFRESH_TOKEN_CATEGORY, null);
//    cookie.setMaxAge(0);
//    cookie.setPath("/");
//    response.addCookie(cookie);
//  }
//}
