package cz.gregetom.graphwiki.user.service

import cz.gregetom.graphwiki.api.user.model.CreateUserTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.user.dao.data.Role
import cz.gregetom.graphwiki.user.dao.data.User
import cz.gregetom.graphwiki.user.dao.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserService(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder
) {

    fun findById(userId: String): User {
        return userRepository.getOne(userId)
    }

    /**
     * Process new user registration.
     *
     * @param createUser new user value
     * @return created user id
     */
    fun register(createUser: CreateUserTO): String {
        LOGGER.info("Register new user, ${createUser.email}")

        this.validateCreateUserRequest(createUser)

        val passwordSalt = UUID.randomUUID().toString()
        return userRepository.save(
                User(
                        id = UUID.randomUUID().toString(),
                        givenName = createUser.givenName,
                        familyName = createUser.familyName,
                        email = createUser.email,
                        password = passwordEncoder.encode(createUser.password + passwordSalt),
                        passwordSalt = passwordSalt,
                        roles = setOf(Roles.ROLE_USER).map { Role(UUID.randomUUID().toString(), it) }.toSet()
                )
        ).id
    }

    /**
     * Check if passwords are equal and requested username does not exist.
     *
     * @param createUser new user value
     * @throws ResponseStatusException if [createUser] is not valid
     */
    private fun validateCreateUserRequest(createUser: CreateUserTO) {
        if (createUser.password != createUser.confirmedPassword) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords not equals!")
        }
        if (!isUsernameValid(createUser.email)) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User with email: ${createUser.email} already exists.")
        }
    }

    /**
     * Check if provided username is unique or not.
     *
     * [username] username
     * @return true if username is unique, otherwise false
     */
    fun isUsernameValid(username: String): Boolean {
        return userRepository.findByEmail(username) === null
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UserService::class.java)
    }
}
