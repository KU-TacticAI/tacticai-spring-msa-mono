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
//  private final SuccessHandler successHandler;
  //  private final FailureHandler failureHandler;
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

//  @Bean
//  public CorsConfigurationSource corsConfigurationSource() {
//    CorsConfiguration config = new CorsConfiguration();
//    config.setAllowCredentials(true);
//    config.setAllowedOriginPatterns(List.of(
//        "http://localhost:3000",
//        "http://127.0.0.1:3000",
//        "http://localhost:5173",
//        "http://127.0.0.1:5173",
//        "https://playcation.store",
//        "https://*.playcation.store"
//    ));
//    config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
//    config.setAllowedHeaders(List.of("*"));
//    // 프론트에서 읽을 필요 있는 헤더만 노출
//    config.setExposedHeaders(List.of("Authorization","Location","Link","X-Total-Count","Set-Cookie"));
//    config.setMaxAge(3600L);
//
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", config);
//    return source;
//  }

//  @Bean
//  public FilterRegistrationBean<CorsFilter> corsFilter(CorsConfigurationSource source) {
//    CorsFilter corsFilter = new CorsFilter(source);
//    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(corsFilter);
//    bean.setOrder(0); // 보안 필터보다 먼저 실행되도록 최우선
//    return bean;
//  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      UserRepository userRepository) throws Exception {
//    OAuth2Service oAuth2Service = applicationContext.getBean(OAuth2Service.class);

//    http.cors(cors -> cors.configurationSource(request -> {
//      CorsConfiguration config = new CorsConfiguration();
//      config.setAllowCredentials(true);
//
//      // 여러 환경 허용 (정확한 문자열 또는 와일드카드 패턴)
//      config.setAllowedOriginPatterns(List.of(
//          frontUrl,                          // 예: https://playcation.store (환경변수/프로퍼티에서 주입)
//          "http://localhost:3000",
//          "http://127.0.0.1:3000",
//          "https://*.playcation.store"
//      ));
//
//      config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//      config.setAllowedHeaders(List.of("*"));
//
//      // 프론트에서 읽을 헤더 노출 (토큰/쿠키/페이지네이션 등 필요한 것만)
//      config.setExposedHeaders(List.of("Authorization", "Location", "Link", "X-Total-Count", "Set-Cookie"));
//
//      // Preflight 캐시
//      config.setMaxAge(3600L);
//      return config;
//    }));

//    http
//        .cors(withDefaults());   // 또는 .cors(c -> c.configurationSource(corsConfigurationSource()))

    http.cors(AbstractHttpConfigurer::disable);

    // csrf disable
    http.csrf(AbstractHttpConfigurer::disable);
    // form 로그인 방식 disable
    http.formLogin(AbstractHttpConfigurer::disable);
    // http basic 인증 방식 disable
    http.httpBasic(AbstractHttpConfigurer::disable);

    http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));

    // oauth2
//    http
//        .oauth2Login((oauth2) -> oauth2
//        .userInfoEndpoint((userInfoEndpointConfig) ->
//            userInfoEndpointConfig.userService(oAuth2Service))
//        .successHandler(successHandler));

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
