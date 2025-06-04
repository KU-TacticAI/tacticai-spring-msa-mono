package com.example.coreservice.config;

import com.example.coreservice.user.repository.UserRepository;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      UserRepository userRepository) throws Exception {

    http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowedOriginPatterns(List.of("*")); // 모든 패턴
      config.addAllowedMethod("*");
      config.addAllowedHeader("*");
      config.setAllowCredentials(true);
      return config;
    }));

    http.csrf(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests((auth) -> auth
        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
        // 전체 허용 API
        .anyRequest().permitAll()
    );

    return http.build();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}