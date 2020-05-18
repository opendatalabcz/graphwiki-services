package cz.gregetom.graphwiki.user.integration.auth

import cz.gregetom.graphwiki.api.user.model.AuthInfo
import cz.gregetom.graphwiki.api.user.model.AuthRequest
import cz.gregetom.graphwiki.api.user.model.AuthResponse
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.user.AbstractIntegrationTest
import cz.gregetom.graphwiki.user.support.data.UserDataSupport
import cz.gregetom.graphwiki.user.web.AuthenticationApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthInfoTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var userDataSupport: UserDataSupport
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun notLoggedAuthInfoTest() {
        val notLoggedAuthInfoResult = httpGet.doGetAndReturnResponse(
                AuthenticationApiLinks.authInfo().toUri(), null
        )
        assertThat(notLoggedAuthInfoResult.contentLength).isZero()
    }

    @Test
    fun authInfoTest() {
        val username = "test@test.com"
        val password = "test123"
        val passwordSalt = "test-salt"
        val user = userDataSupport.createUserWithPasswordAndRoles(username, passwordEncoder.encode(password + passwordSalt), passwordSalt, Roles.ROLE_USER)

        val authRequest = AuthRequest(username, password)
        val authResponse = httpPost.doPostForObject(AuthResponse::class, AuthenticationApiLinks.authenticate().toUri(), authRequest, null)

        val loggedAuthInfo = objectMapper.readValue(
                mockMvc.perform(get(AuthenticationApiLinks.authInfo().toUri())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer ${authResponse.token}"))
                        .andDo(print())
                        .andExpect(status().isOk)
                        .andReturn()
                        .response
                        .contentAsString,
                AuthInfo::class.java
        )

        assertThat(loggedAuthInfo).isNotNull
        assertThat(loggedAuthInfo).isEqualToComparingOnlyGivenFields(user, "givenName", "familyName", "email")
    }
}
