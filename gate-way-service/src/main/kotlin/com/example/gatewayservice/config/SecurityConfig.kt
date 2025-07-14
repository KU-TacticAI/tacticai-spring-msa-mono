package com.example.gatewayservice.config

import com.example.gatewayservice.filter.JWTFilter
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val jwtFilter: JWTFilter
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("api/**","/email", "/mail-check", "/oauth2/**", "*/sign-in", "/oauth2-login", "/refresh", "/error", "/token/refresh", "/api*", "/api-docs/**", "/swagger-ui/**", "/v3/**")
                    .permitAll()
                    .anyExchange().authenticated()
            }
            .build()
    }

    @Bean
    fun globalFilter(): GlobalFilter {
        return jwtFilter
    }
}
