package com.example.gatewayservice.filter

import com.example.commonmodule.common.TokenSettings
import com.example.commonmodule.exceptions.InvalidInputException
import com.example.commonmodule.exceptions.TokenErrorCode
import com.example.commonmodule.util.JWTUtil
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

class JWTFilter(
//    private val tokenService: TokenService,
    private val jwtUtil: JWTUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestUri = request.requestURI
        if (isLoginRequest(requestUri) || isSignInRequest(requestUri)) {
            filterChain.doFilter(request, response)
            return
        }

        var accessToken = request.getHeader(TokenSettings.ACCESS_TOKEN_CATEGORY)
        if (accessToken == null) {
            filterChain.doFilter(request, response)
            return
        }

        accessToken = accessToken.removePrefix("Bearer ")

        try {
            authenticateUser(accessToken)
            validateToken(accessToken)
        } catch (e: ExpiredJwtException) {
            val refreshToken = request.cookies
                ?.firstOrNull { it.name == TokenSettings.REFRESH_TOKEN_CATEGORY }
                ?.value

            try {
                if (refreshToken != null) {
                    authenticateUser(refreshToken)
                    validateToken(refreshToken)

//                    val (newAccessToken, newRefreshToken) = tokenService.createNewToken(request)
//                    response.addCookie(jwtUtil.createCookie(TokenSettings.REFRESH_TOKEN_CATEGORY, newRefreshToken))

                    response.status = HttpServletResponse.SC_OK
                    response.contentType = MediaType.APPLICATION_JSON_VALUE
//                    response.writer.write("""{"token": "$newAccessToken"}""")
                    return
                } else {
                    sendErrorResponse(response, "리프레시 토큰이 없습니다.")
                    return
                }
            } catch (ex: Exception) {
                sendErrorResponse(response, "잘못된 리프레시 토큰입니다.")
                return
            }
        } catch (e: Exception) {
            sendErrorResponse(response, "잘못된 토큰입니다.")
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun isLoginRequest(uri: String): Boolean {
        return uri.matches(Regex(".*/login(?:/.*)?$")) || uri.matches(Regex(".*/oauth2(?:/.*)?$"))
    }

    private fun isSignInRequest(uri: String): Boolean {
        return uri.matches(Regex(".*/sign-in(?:/.*)?$"))
    }

    private fun validateToken(token: String) {
        jwtUtil.isExpired(token)
        jwtUtil.isIssuer(token)
        val category = jwtUtil.getCategory(token)
        if (category != TokenSettings.ACCESS_TOKEN_CATEGORY) {
            throw InvalidInputException(TokenErrorCode.TOKEN_CATEGORY_MISS_MATCH)
        }
    }

    private fun authenticateUser(token: String) {
        val userId = jwtUtil.getUserId(token).toLong()
    }

    private fun sendErrorResponse(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.use { it.print(message) }
    }
}