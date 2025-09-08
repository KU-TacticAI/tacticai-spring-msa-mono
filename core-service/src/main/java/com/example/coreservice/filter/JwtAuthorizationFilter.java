package com.example.coreservice.filter;

import com.example.commonmodule.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JwtAuthorizationFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // 리프레시와 로그인, 기타 공개 경로는 인증필터 적용하지 않음
        return uri.equals("/token/refresh") || uri.equals("/login") /* || uri.startsWith("/public") */;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authz = request.getHeader("Authorization");
        if (authz == null || !authz.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.replace("Bearer ", "");

        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getAuth(token);
        String currentSid = jwtUtil.getSessionId(token);
        String tokenSid = jwtUtil.getCurrentSession(userId, role);

        if (currentSid == null || !currentSid.equals(tokenSid)) {
            // 다른 곳에서 새 로그인 발생 → 이 토큰 무효
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        var authToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
