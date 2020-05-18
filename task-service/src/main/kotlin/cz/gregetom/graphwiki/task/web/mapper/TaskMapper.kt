package cz.gregetom.graphwiki.task.web.mapper

import cz.gregetom.graphwiki.api.task.model.TaskState
import cz.gregetom.graphwiki.api.task.model.TaskTO
import cz.gregetom.graphwiki.api.task.model.TaskTOLinks
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.task.dao.data.Task
import cz.gregetom.graphwiki.task.web.LinkFactory
import cz.gregetom.graphwiki.task.web.TaskApiLinks
import cz.gregetom.graphwiki.task.web.toLinkTO
import org.springframework.stereotype.Component

@Component
class TaskMapper(private val linkFactory: LinkFactory) {

    fun map(task: Task): TaskTO {
        return TaskTO(
                id = task.id,
                state = task.state,
                label = task.entityLabel,
                type = task.type,
                author = linkFactory.userById(task.author, "author").toLinkTO(),
                assignee = task.assignee?.let { linkFactory.userById(task.assignee, "assignee").toLinkTO() },
                created = task.created,
                links = mapLinks(task)
        )
    }

    private fun mapLinks(task: Task): TaskTOLinks {
        return TaskTOLinks(
                entity = task.entityUrl.toLinkTO("self"),
                assign =
                if (task.assignee === null && task.state !== TaskState.DONE && UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) {
                    TaskApiLinks.taskAssign(task.id, UserAccessor.currentUserIdOrThrow).toLinkTO()
                } else null,
                unassign = if (task.state !== TaskState.DONE && UserAccessor.currentUserIs(task.assignee) && UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) {
                    TaskApiLinks.taskAssign(task.id, null).toLinkTO()
                } else null
        )
    }
}
