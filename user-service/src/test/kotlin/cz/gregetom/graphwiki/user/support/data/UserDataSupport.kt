package cz.gregetom.graphwiki.user.support.data

import cz.gregetom.graphwiki.commons.test.RandomGenerator
import cz.gregetom.graphwiki.user.dao.data.Role
import cz.gregetom.graphwiki.user.dao.data.User
import cz.gregetom.graphwiki.user.dao.repository.UserRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserDataSupport(private val userRepository: UserRepository) {

    fun createUserWithRoles(email: String, vararg roles: String): User {
        return userRepository.save(
                User(
                        id = UUID.randomUUID().toString(),
                        givenName = RandomGenerator.instance.nextObject(String::class.java),
                        familyName = RandomGenerator.instance.nextObject(String::class.java),
                        email = email,
                        password = RandomGenerator.randomString(30),
                        passwordSalt = RandomGenerator.randomString(30),
                        roles = roles.map { Role(UUID.randomUUID().toString(), it) }.toSet()
                )
        )
    }

    fun createUserWithPasswordAndRoles(email: String, password: String, passwordSalt: String, vararg roles: String): User {
        return userRepository.save(
                User(
                        id = UUID.randomUUID().toString(),
                        givenName = RandomGenerator.instance.nextObject(String::class.java),
                        familyName = RandomGenerator.instance.nextObject(String::class.java),
                        email = email,
                        password = password,
                        passwordSalt = passwordSalt,
                        roles = roles.map { Role(UUID.randomUUID().toString(), it) }.toSet()
                )
        )
    }
}
