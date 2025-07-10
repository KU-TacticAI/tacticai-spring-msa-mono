package com.example.gatewayservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableReactiveMethodSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/email", "/mail-check", "/oauth2/**", "*/sign-in", "/oauth2-login", "/refresh", "/error", "/token/refresh", "/api*", "/api-docs/**", "/swagger-ui/**", "/v3/**")
                    .permitAll()
                    .anyExchange().permitAll()  // 실제 운영에서는 .authenticated() 권장
            }
            .build()
    }
}
