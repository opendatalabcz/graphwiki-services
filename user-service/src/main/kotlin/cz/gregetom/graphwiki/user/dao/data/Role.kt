package cz.gregetom.graphwiki.user.dao.data

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "APP_ROLE")
data class Role(
        @Id
        val id: String,
        val name: String
)
