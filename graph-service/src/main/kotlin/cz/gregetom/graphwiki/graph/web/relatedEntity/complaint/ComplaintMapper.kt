package cz.gregetom.graphwiki.graph.web.relatedEntity.complaint

import cz.gregetom.graphwiki.api.graph.model.ComplaintState
import cz.gregetom.graphwiki.api.graph.model.ComplaintTO
import cz.gregetom.graphwiki.api.graph.model.ComplaintTOLinks
import cz.gregetom.graphwiki.api.graph.model.LinkTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.graph.dao.jpa.data.Complaint
import cz.gregetom.graphwiki.graph.web.ComplaintApiLinks
import cz.gregetom.graphwiki.graph.web.GraphEntityApiLinks
import cz.gregetom.graphwiki.graph.web.LinkFactory
import cz.gregetom.graphwiki.graph.web.toLinkTO
import org.springframework.stereotype.Component

@Component
class ComplaintMapper(private val linkFactory: LinkFactory) {

    fun map(complaint: Complaint): ComplaintTO {
        return ComplaintTO(
                id = complaint.id,
                author = linkFactory.userSelf(complaint.author, "author").toLinkTO(),
                assignee = if (UserAccessor.currentUserIs(complaint.assignee)) linkFactory.userSelf(complaint.assignee!!, "assignee").toLinkTO() else null,
                title = complaint.title,
                explanation = complaint.explanation,
                state = complaint.state,
                created = complaint.created,
                links = mapLinks(complaint)
        )
    }

    private fun mapLinks(complaint: Complaint): ComplaintTOLinks {
        return ComplaintTOLinks(
                self = ComplaintApiLinks.self(complaint.id).toLinkTO(),
                entity = GraphEntityApiLinks.selfByType(complaint.entityId, complaint.entityType).toLinkTO(),
                task = complaint.taskUrl?.toLinkTO("task"),
                related = ComplaintApiLinks.complaintRelated(complaint.id).toLinkTO(),
                commentList = linkFactory.commentList(complaint.id).toLinkTO(),
                commentCreate =
                if (complaint.state === ComplaintState.ADMIN_DECISION && UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) {
                    linkFactory.commentCreate(complaint.id).toLinkTO()
                } else {
                    null
                },
                approve = complaint.ifStateTransitionAvailable { ComplaintApiLinks.approve(complaint.id).toLinkTO() },
                reject = complaint.ifStateTransitionAvailable { ComplaintApiLinks.reject(complaint.id).toLinkTO() }
        )
    }
}

private fun Complaint.ifStateTransitionAvailable(block: () -> LinkTO?): LinkTO? {
    return if (this.state === ComplaintState.ADMIN_DECISION && UserAccessor.currentUserIs(this.assignee) && UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) {
        block()
    } else {
        null
    }
}
