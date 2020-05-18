package cz.gregetom.graphwiki.user.dao.repository

import cz.gregetom.graphwiki.user.dao.data.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {

    fun findByEmail(email: String): User?
}
