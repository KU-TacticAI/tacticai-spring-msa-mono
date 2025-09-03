package com.example.gameservice.filter

import com.example.commonmodule.util.JWTUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Collections

class JwtAuthorizationFilter(private val jwtUtil: JWTUtil) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authorizationHeader.replace("Bearer ", "")

        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response)
            return
        }

        val userId = jwtUtil.getUserId(token)
        val role = jwtUtil.getAuth(token) // Using getAuth as found in JWTUtil

        val authorities = Collections.singletonList(SimpleGrantedAuthority(role))
        val authToken = UsernamePasswordAuthenticationToken(userId, null, authorities)
        SecurityContextHolder.getContext().authentication = authToken

        filterChain.doFilter(request, response)
    }
}
