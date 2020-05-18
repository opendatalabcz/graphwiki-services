package cz.gregetom.graphwiki.user.web.controller

import cz.gregetom.graphwiki.api.user.api.AuthenticationApi
import cz.gregetom.graphwiki.api.user.model.AuthInfo
import cz.gregetom.graphwiki.api.user.model.AuthRequest
import cz.gregetom.graphwiki.api.user.model.AuthResponse
import cz.gregetom.graphwiki.user.service.AuthenticationService
import cz.gregetom.graphwiki.user.web.RequestMappingConstants
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping
@Validated
class AuthenticationController(
        private val authenticationService: AuthenticationService,
        private val request: HttpServletRequest
) : AuthenticationApi {

    @Transactional(readOnly = true)
    override fun authenticate(@Valid @RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authenticationService.processAuthenticationRequest(authRequest))
    }

    /**
     * This endpoint is not provided with [AuthenticationApi], because of nullable response type.
     */
    @GetMapping(RequestMappingConstants.AC_AUTH_INFO)
    @Transactional(readOnly = true)
    fun authInfo(): ResponseEntity<AuthInfo?> {
        return ResponseEntity.ok().body(authenticationService.getAuthInfo(request))
    }
}
