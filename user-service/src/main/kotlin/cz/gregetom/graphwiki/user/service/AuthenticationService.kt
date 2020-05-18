package cz.gregetom.graphwiki.user.service

import cz.gregetom.graphwiki.api.user.model.AuthInfo
import cz.gregetom.graphwiki.api.user.model.AuthRequest
import cz.gregetom.graphwiki.api.user.model.AuthResponse
import cz.gregetom.graphwiki.commons.security.enums.TokenClaims
import cz.gregetom.graphwiki.commons.security.service.JwtTokenService
import cz.gregetom.graphwiki.commons.security.util.HttpHeaderUtil
import cz.gregetom.graphwiki.user.dao.repository.UserRepository
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class AuthenticationService(
        private val authenticationManager: AuthenticationManager,
        private val jwtTokenService: JwtTokenService,
        private val userRepository: UserRepository
) {

    /**
     * Try authenticate user.
     * If authentication is successful, generate JWT token.
     *
     * @param authRequest username and password
     * @throws UsernameNotFoundException if user does not exist
     * @throws AuthenticationException if authentication failed
     * @return authInfo and JWT token
     */
    fun processAuthenticationRequest(authRequest: AuthRequest): AuthResponse {
        val user = userRepository.findByEmail(authRequest.username)
                ?: throw UsernameNotFoundException("Username ${authRequest.username} not found!")
        doAuthenticate(authRequest, user.passwordSalt)
        val claims: Map<String, Any> = mutableMapOf(
                TokenClaims.USER_ID to user.id,
                TokenClaims.AUTHORITIES to user.roles.map { it.name }
        )
        return AuthResponse(
                authInfo = AuthInfo(user.id, user.givenName, user.familyName, user.email),
                token = jwtTokenService.generateToken(claims, authRequest.username)
        )
    }

    /**
     * Resolve AuthInfo from authorization token.
     *
     * @param request HttpRequest
     * @return AuthInfo resolved from token, if token is not present, then null
     */
    fun getAuthInfo(request: HttpServletRequest): AuthInfo? {
        try {
            return HttpHeaderUtil.getTokenFromHttpRequest(request)
                    ?.let { jwtTokenService.getUsernameFromToken(it) }
                    ?.let { userRepository.findByEmail(it) }
                    ?.let { AuthInfo(it.id, it.givenName, it.familyName, it.email) }
        } catch (e: IllegalArgumentException) {
        } catch (e: ExpiredJwtException) {
        }
        return null
    }

    /**
     * Authenticate user.
     *
     * @param authRequest username and password
     * @param passwordSalt salt for password
     * @throws AuthenticationException if authentication failed
     */
    @Throws(AuthenticationException::class)
    private fun doAuthenticate(request: AuthRequest, passwordSalt: String) {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password + passwordSalt))
    }
}
