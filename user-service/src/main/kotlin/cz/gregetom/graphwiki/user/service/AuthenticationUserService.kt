package cz.gregetom.graphwiki.user.service

import cz.gregetom.graphwiki.user.dao.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthenticationUserService(private val userRepository: UserRepository) : UserDetailsService {

    /**
     * Load user by username for authentication process.
     *
     * @param username username of requested user
     * @throws UsernameNotFoundException if user does not exist
     * @return user with username, password and authorities
     */
    @Throws(UsernameNotFoundException::class)
    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
                ?: throw UsernameNotFoundException("Username $username not found!")
        return User(user.email, user.password, user.roles.map { GrantedAuthority { it.name } })
    }
}
