package com.example.gatewayservice.config

import com.example.commonmodule.util.JWTUtil
import com.example.gatewayservice.filter.CustomLoginFilter
import com.example.gatewayservice.filter.JWTFilter
import jakarta.servlet.DispatcherType
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.core.token.TokenService

@Configuration
@EnableWebSecurity(debug = true)
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val applicationContext: ApplicationContext,
    private val tokenService: TokenService,
    private val successHandler: CustomSuccessHandler,
    private val jwtUtil: JWTUtil
) {

    @Value("\${spring.profiles.front_url}")
    private lateinit var frontUrl: String

    private val whiteList = arrayOf(
        "/", "/email", "/mail-check", "/oauth2/**", "*/sign-in", "/oauth2-login", "/refresh", "/error",
        "/token/refresh", "/h2-console/**", "/api*", "/api-docs/**", "swagger-ui/**", "v3/**"
    )

    private val adminList = arrayOf("/admin/**", "/users/{id}/update-role")
    private val managerList = arrayOf("/manager/**")

    @Bean
    fun authenticationManager(): AuthenticationManager =
        authenticationConfiguration.authenticationManager

    @Bean
    fun roleHierarchy(): RoleHierarchy {
        val hierarchy = RoleHierarchyImpl()
        hierarchy.setHierarchy(
            "${Role.ADMIN} > ${Role.MANAGER}\n${Role.MANAGER} > ${Role.USER}"
        )
        return hierarchy
    }

    @Bean
    fun configure(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(
                AntPathRequestMatcher("/h2-console/**"),
                AntPathRequestMatcher("/img/**"),
                AntPathRequestMatcher("/css/**"),
                AntPathRequestMatcher("/js/**")
            )
        }
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity, userRepository: UserRepository): SecurityFilterChain {
        val oAuth2Service = applicationContext.getBean(OAuth2Service::class.java)

        http.cors { cors ->
            cors.configurationSource {
                CorsConfiguration().apply {
                    addAllowedOrigin(frontUrl)
                    addAllowedMethod("*")
                    addAllowedHeader("*")
                    allowCredentials = true
                }
            }
        }

        http.csrf { it.disable() }
        http.formLogin { it.disable() }
        http.httpBasic { it.disable() }
        http.headers { it.frameOptions(FrameOptionsConfig::disable) }

        http.oauth2Login { oauth2 ->
            oauth2
                .userInfoEndpoint { it.userService(oAuth2Service) }
                .successHandler(successHandler)
        }

        http.authorizeHttpRequests { auth ->
            auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            auth.requestMatchers(*whiteList).permitAll()
            auth.requestMatchers(*adminList).hasAuthority(Role.ADMIN.name)
            auth.requestMatchers(*managerList).hasAuthority(Role.MANAGER.name)
            auth.requestMatchers("/carts/**").access { authz, _ ->
                org.springframework.security.authorization.AuthorizationDecision(
                    authz.get().authorities.none { it.authority == Role.ADMIN.name }
                )
            }
            auth.anyRequest().authenticated()
        }

        http.addFilterBefore(JWTFilter(jwtUtil), CustomLoginFilter::class.java)
//        http.addFilterBefore(CustomLogoutFilter(jwtUtil), LogoutFilter::class.java)
//        http.addFilterAt(CustomLoginFilter(authenticationManager(), jwtUtil), UsernamePasswordAuthenticationFilter::class.java)

        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        return http.build()
    }
}