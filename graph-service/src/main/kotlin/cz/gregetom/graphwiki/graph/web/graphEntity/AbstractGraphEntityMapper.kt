package cz.gregetom.graphwiki.graph.web.graphEntity

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.LinkTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphEntity

abstract class AbstractGraphEntityMapper {

    protected fun GraphEntity.ifComplaintAvailable(block: () -> LinkTO?): LinkTO? {
        return if (this.state === GraphEntityState.ACTIVE && UserAccessor.hasRoleAny(Roles.ROLE_USER, Roles.ROLE_ADMIN)) {
            block()
        } else {
            null
        }
    }

    protected fun GraphEntity.ifRestoreAvailable(block: () -> LinkTO?): LinkTO? {
        return if (this.state === GraphEntityState.DELETED && UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) {
            block()
        } else {
            null
        }
    }

    protected fun GraphEntity.ifUpdateAvailable(block: () -> LinkTO?): LinkTO? {
        return if (this.state === GraphEntityState.CONCEPT || this.state === GraphEntityState.ACTIVE && UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) {
            block()
        } else {
            null
        }
    }

    protected fun GraphEntity.ifDeleteAvailable(block: () -> LinkTO?): LinkTO? {
        return if (this.state !== GraphEntityState.DELETED && UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) {
            block()
        } else {
            null
        }
    }
}
