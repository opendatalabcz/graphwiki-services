package cz.gregetom.graphwiki.user.integration.auth

import cz.gregetom.graphwiki.api.user.model.AuthRequest
import cz.gregetom.graphwiki.api.user.model.AuthResponse
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.test.RandomGenerator
import cz.gregetom.graphwiki.user.AbstractIntegrationTest
import cz.gregetom.graphwiki.user.support.data.UserDataSupport
import cz.gregetom.graphwiki.user.web.AuthenticationApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder

class AuthenticateTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var userDataSupport: UserDataSupport
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun authenticateTest() {
        val username = "test@test.com"
        val password = "test123"
        val passwordSalt = "test-salt"
        val user = userDataSupport.createUserWithPasswordAndRoles(username, passwordEncoder.encode(password + passwordSalt), passwordSalt, Roles.ROLE_USER)

        val authRequest = AuthRequest(username, password)
        val authResponse = httpPost.doPostForObject(AuthResponse::class, AuthenticationApiLinks.authenticate().toUri(), authRequest, null)

        assertThat(authResponse.authInfo.id).isNotNull()
        assertThat(authResponse.authInfo).isEqualToComparingOnlyGivenFields(user, "givenName", "familyName", "email")
        assertThat(authResponse.token).isNotNull()
    }

    @Test
    fun authenticateFailedTest() {
        val username = "test@test.com"
        val password = "test123"
        val passwordSalt = "test-salt"
        userDataSupport.createUserWithPasswordAndRoles(username, passwordEncoder.encode(password + passwordSalt), passwordSalt, Roles.ROLE_USER)

        val invalidPasswordAuthRequest = AuthRequest(username, password.reversed())
        httpPost.doPostAndExpect(AuthenticationApiLinks.authenticate().toUri(), invalidPasswordAuthRequest, HttpStatus.UNAUTHORIZED)

        val invalidUsernameAuthRequest = AuthRequest(username.reversed(), password)
        httpPost.doPostAndExpect(AuthenticationApiLinks.authenticate().toUri(), invalidUsernameAuthRequest, HttpStatus.UNAUTHORIZED)

        val notExistingUsernameAuthRequest = AuthRequest("not-existing@test.com", password)
        httpPost.doPostAndExpect(AuthenticationApiLinks.authenticate().toUri(), notExistingUsernameAuthRequest, HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun authenticateBadRequestTest() {
        val username = "test@test.com"
        val password = "test123"

        val tooShortPasswordAuthRequest = AuthRequest(username, "aaa")
        httpPost.doPostAndExpect(AuthenticationApiLinks.authenticate().toUri(), tooShortPasswordAuthRequest, HttpStatus.BAD_REQUEST)

        val tooLongUsernameAuthRequest = AuthRequest(RandomGenerator.randomString(100), password)
        httpPost.doPostAndExpect(AuthenticationApiLinks.authenticate().toUri(), tooLongUsernameAuthRequest, HttpStatus.BAD_REQUEST)
    }
}
