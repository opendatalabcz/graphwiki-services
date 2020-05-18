package cz.gregetom.graphwiki.graph.web.relatedEntity.entityRequest

import cz.gregetom.graphwiki.api.graph.model.EntityRequestState
import cz.gregetom.graphwiki.api.graph.model.EntityRequestTO
import cz.gregetom.graphwiki.api.graph.model.EntityRequestTOLinks
import cz.gregetom.graphwiki.api.graph.model.LinkTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.graph.dao.jpa.data.EntityRequest
import cz.gregetom.graphwiki.graph.web.EntityRequestApiLinks
import cz.gregetom.graphwiki.graph.web.GraphEntityApiLinks
import cz.gregetom.graphwiki.graph.web.LinkFactory
import cz.gregetom.graphwiki.graph.web.toLinkTO
import org.springframework.stereotype.Component

@Component
class EntityRequestMapper(private val linkFactory: LinkFactory) {

    fun map(entityRequest: EntityRequest): EntityRequestTO {
        return EntityRequestTO(
                id = entityRequest.id,
                author = linkFactory.userSelf(entityRequest.author, "author").toLinkTO(),
                created = entityRequest.created,
                state = entityRequest.state,
                assignee = entityRequest.assignee?.let { linkFactory.userSelf(it, "assignee").toLinkTO() },
                links = mapLinks(entityRequest)
        )
    }

    private fun mapLinks(entityRequest: EntityRequest): EntityRequestTOLinks {
        return EntityRequestTOLinks(
                self = EntityRequestApiLinks.self(entityRequest.id).toLinkTO(),
                entity = GraphEntityApiLinks.selfByType(entityRequest.entityId, entityRequest.entityType).toLinkTO(),
                task = entityRequest.taskUrl?.toLinkTO("task"),
                approve = entityRequest.ifStateTransitionAvailable { EntityRequestApiLinks.approve(entityRequest.id).toLinkTO() },
                reject = entityRequest.ifStateTransitionAvailable { EntityRequestApiLinks.reject(entityRequest.id).toLinkTO() },
                commentCreate =
                if (entityRequest.state === EntityRequestState.NEW && UserAccessor.hasRoleAny(Roles.ROLE_USER, Roles.ROLE_ADMIN)) {
                    linkFactory.commentCreate(entityRequest.id).toLinkTO()
                } else {
                    null
                },
                commentList = linkFactory.commentList(entityRequest.id).toLinkTO()
        )
    }
}

fun EntityRequest.ifStateTransitionAvailable(block: () -> LinkTO?): LinkTO? {
    return if (this.state === EntityRequestState.NEW && UserAccessor.currentUserIs(this.assignee) && UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) {
        block()
    } else {
        null
    }
}
