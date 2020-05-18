package cz.gregetom.graphwiki.user.integration.user

import cz.gregetom.graphwiki.api.user.model.CreateUserTO
import cz.gregetom.graphwiki.api.user.model.UserTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.test.RandomGenerator
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.user.AbstractIntegrationTest
import cz.gregetom.graphwiki.user.web.UserApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class CrudTest : AbstractIntegrationTest() {

    @Test
    fun crudTest() {
        val password = RandomGenerator.randomString(30)
        val createUser = CreateUserTO(
                givenName = RandomGenerator.instance.nextObject(String::class.java),
                familyName = RandomGenerator.instance.nextObject(String::class.java),
                email = "test@test.com",
                password = password,
                confirmedPassword = password
        )
        val location = httpPost.doPost(UserApiLinks.register().toUri(), createUser)

        val userByAdmin = httpGet.doGet(UserTO::class, location, TestUsers.ADMIN)
        assertThat(userByAdmin.id).isNotNull()
        assertThat(userByAdmin.givenName).isEqualTo(createUser.givenName)
        assertThat(userByAdmin.familyName).isEqualTo(createUser.familyName)
        assertThat(userByAdmin.email).isEqualTo(createUser.email)

        val userByCreateUser = httpGet.doGet(UserTO::class, location,
                User("test@test.com", password, listOf(GrantedAuthority { Roles.ROLE_USER })))
        assertThat(userByCreateUser.id).isNotNull()
        assertThat(userByCreateUser.givenName).isEqualTo(createUser.givenName)
        assertThat(userByCreateUser.familyName).isEqualTo(createUser.familyName)
        assertThat(userByAdmin.email).isEqualTo(createUser.email)

        val userByAnotherUser = httpGet.doGet(UserTO::class, location, TestUsers.USER_ANOTHER)
        assertThat(userByAnotherUser.id).isNotNull()
        assertThat(userByAnotherUser.givenName).isEqualTo(createUser.givenName)
        assertThat(userByAnotherUser.familyName).isEqualTo(createUser.familyName)
        assertThat(userByAnotherUser.email).isNull()

        httpPost.doPostAndExpect(UserApiLinks.register().toUri(), createUser, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun createBadRequestTest() {
        val password = RandomGenerator.randomString(30)
        val validCreateUser = CreateUserTO(
                givenName = RandomGenerator.instance.nextObject(String::class.java),
                familyName = RandomGenerator.instance.nextObject(String::class.java),
                email = "test@test.com",
                password = password,
                confirmedPassword = password
        )

        val invalidEmailCreateUser = validCreateUser.copy(email = "invalid-email")
        httpPost.doPostAndExpect(UserApiLinks.register().toUri(), invalidEmailCreateUser, HttpStatus.BAD_REQUEST)

        val differentPasswordsCreateUser = validCreateUser.copy(confirmedPassword = validCreateUser.password.reversed())
        httpPost.doPostAndExpect(UserApiLinks.register().toUri(), differentPasswordsCreateUser, HttpStatus.BAD_REQUEST)

        val tooShortPasswordCreateUser = validCreateUser.copy(password = "abc")
        httpPost.doPostAndExpect(UserApiLinks.register().toUri(), tooShortPasswordCreateUser, HttpStatus.BAD_REQUEST)

        val tooLongFamilyName = validCreateUser.copy(familyName = RandomGenerator.randomString(100))
        httpPost.doPostAndExpect(UserApiLinks.register().toUri(), tooLongFamilyName, HttpStatus.BAD_REQUEST)
    }
}
