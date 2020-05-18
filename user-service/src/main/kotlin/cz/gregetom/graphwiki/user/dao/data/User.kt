package cz.gregetom.graphwiki.user.dao.data

import javax.persistence.*

@Entity
@Table(name = "APP_USER")
data class User(
        @Id
        val id: String,
        val givenName: String,
        val familyName: String,
        val email: String,
        val password: String,
        val passwordSalt: String,
        @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @JoinColumn(name = "user_id")
        var roles: Set<Role> = emptySet()
)
