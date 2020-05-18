package cz.gregetom.graphwiki.task.services

import cz.gregetom.graphwiki.api.task.model.CreateTaskTO
import cz.gregetom.graphwiki.api.task.model.TaskState
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.commons.web.InterCommunicationRestTemplate
import cz.gregetom.graphwiki.task.dao.data.Task
import cz.gregetom.graphwiki.task.dao.repository.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@Service
class TaskService(
        private val taskRepository: TaskRepository,
        private val restTemplate: InterCommunicationRestTemplate
) {

    /**
     * Create new task.
     *
     * @param createTaskTO new task value
     * @return created task
     */
    fun save(createTaskTO: CreateTaskTO): Task {
        LOGGER.info("Create new task $createTaskTO")
        return taskRepository.save(
                Task(
                        id = UUID.randomUUID().toString(),
                        author = createTaskTO.author,
                        created = createTaskTO.created,
                        state = TaskState.OPEN,
                        type = createTaskTO.type,
                        entityId = createTaskTO.entityId,
                        entityLabel = createTaskTO.entityLabel,
                        entityUrl = createTaskTO.entityUrl,
                        assignUrl = createTaskTO.assignUrl,
                        assigneeRole = createTaskTO.assigneeRole
                )
        )
    }

    /**
     * Get count of open tasks for current user.
     *
     * @return the count
     */
    fun openTaskCount(): Long {
        return taskRepository.countAllByAssigneeAndStateIn(UserAccessor.currentUserIdOrThrow, TaskState.ASSIGNED) +
                taskRepository.countAllByAssigneeRoleInAndStateIn(UserAccessor.currentUserRolesOrThrow, TaskState.OPEN)
    }

    /**
     * Find all tasks for team inbox of current user, sorted by creation datetime.
     *
     * @param showAssigned Indicates whether assigned tasks will be present in output or not.
     * @return team inbox tasks
     */
    fun findAllForUserRole(showAssigned: Boolean): List<Task> {
        val tasks = if (showAssigned) {
            taskRepository.findAllByAssigneeRoleInAndStateIn(UserAccessor.currentUserRolesOrThrow, TaskState.ASSIGNED, TaskState.OPEN)
        } else {
            taskRepository.findAllByAssigneeRoleInAndAssigneeIsNullAndStateIn(UserAccessor.currentUserRolesOrThrow, TaskState.OPEN)
        }
        return tasks.sortedByDescending { it.created }
    }

    /**
     * Get assigned tasks to current user.
     *
     * @return assigned tasks
     */
    fun findAllAssignedToUser(): List<Task> {
        return taskRepository.findAllByAssigneeAndStateIsNot(UserAccessor.currentUserIdOrThrow, TaskState.DONE).sortedByDescending { it.created }
    }

    /**
     * Assign task and related entity (e.g. complaint, entity-request,...).
     *
     * @param id id of task to be assigned
     * @param assignee new assignee, nullable
     * @throws ResponseStatusException if task is done
     */
    fun assign(id: String, assignee: String?) {
        LOGGER.info("Assign task $id to $assignee")
        val task = taskRepository.getOne(id)
                .apply {
                    if (assignee !== null && this.state === TaskState.ASSIGNED)
                        throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot assign ASSIGNED task.")
                }
                .apply {
                    if (this.state === TaskState.DONE)
                        throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot assign DONE task.")
                }
                .let {
                    taskRepository.save(it.copy(
                            assignee = assignee,
                            state = assignee?.let { TaskState.ASSIGNED } ?: TaskState.OPEN
                    ))
                }

        LOGGER.info("Assign task $id related entity (${task.entityUrl}) to $assignee")
        restTemplate.putAsTechnicalUser(
                UriComponentsBuilder.fromUri(task.assignUrl).apply {
                    assignee?.let { this.queryParam("assignee", assignee) }
                }.build(emptyMap<Any, Any>())
        )
    }

    fun findById(id: String): Task {
        return taskRepository.getOne(id)
    }

    /**
     * Finish open task.
     *
     * @param id id of task to be finished
     * @param userId id of user who triggered the action
     * @throws ResponseStatusException if task is already done or [userId] is not assignee
     */
    fun finish(id: String, userId: String) {
        LOGGER.info("Finish task $id by user $userId")
        val task = findById(id)
                .apply { if (this.state === TaskState.DONE) throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot change state of DONE task.") }
                .apply {
                    if (this.state === TaskState.ASSIGNED && this.assignee != userId)
                        throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "You are not an assignee.")
                }
        taskRepository.save(task.copy(state = TaskState.DONE))
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TaskService::class.java)
    }
}
