package cz.gregetom.graphwiki.user.web.mapper

import cz.gregetom.graphwiki.api.user.model.UserTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.user.dao.data.User
import org.springframework.stereotype.Component

@Component
class UserMapper {

    fun map(user: User): UserTO {
        return UserTO(
                id = user.id,
                givenName = user.givenName,
                familyName = user.familyName,
                email = if (UserAccessor.hasRoleAny(Roles.ROLE_ADMIN) || UserAccessor.currentUserIs(user.id)) user.email else null
        )
    }
}
