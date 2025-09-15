package com.example.coreservice.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.example.commonmodule.util.JWTUtil;
import com.example.coreservice.auth.service.TokenService;
import com.example.coreservice.enums.Role;
import com.example.coreservice.filter.CustomLoginFilter;
import com.example.coreservice.filter.CustomLogoutFilter;
import com.example.coreservice.filter.JwtAuthorizationFilter;
import com.example.coreservice.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final ApplicationContext applicationContext;
  private final TokenService tokenService;
  private final JWTUtil jwtUtil;

  @Value("${spring.profiles.front_url}")
  private String frontUrl;

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy(
        Role.ADMIN + " > " + Role.USER
    );
    return roleHierarchy;
  }

  @Bean
  public WebSecurityCustomizer configure() {
    return (web) -> web.ignoring()
        .requestMatchers(
            new AntPathRequestMatcher("/h2-console/**"),  // H2 콘솔 직접 설정
            new AntPathRequestMatcher("/img/**"),
            new AntPathRequestMatcher("/css/**"),
            new AntPathRequestMatcher("/js/**")
        );
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      UserRepository userRepository) throws Exception {

    http.cors(AbstractHttpConfigurer::disable);

    // csrf disable
    http.csrf(AbstractHttpConfigurer::disable);
    // form 로그인 방식 disable
    http.formLogin(AbstractHttpConfigurer::disable);
    // http basic 인증 방식 disable
    http.httpBasic(AbstractHttpConfigurer::disable);

    http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));

    http.authorizeHttpRequests((auth) -> auth
        .requestMatchers(
            "/login",
            "/users/sign-in",
            "/token/refresh",
            "/h2-console/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/api/internal/**"
        ).permitAll()
        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
        .anyRequest().hasAuthority(Role.USER.name())
    );

    http.addFilterBefore(new CustomLogoutFilter(jwtUtil), LogoutFilter.class);
    http.addFilterBefore(new JwtAuthorizationFilter(jwtUtil), CustomLoginFilter.class);
    http.addFilterAt(
        new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
        UsernamePasswordAuthenticationFilter.class);

    // 세션 설정
    http.sessionManagement((session) ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    return http.build();
  }
}
