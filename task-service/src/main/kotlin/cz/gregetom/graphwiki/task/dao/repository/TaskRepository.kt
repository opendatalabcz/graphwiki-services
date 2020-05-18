package cz.gregetom.graphwiki.task.dao.repository

import cz.gregetom.graphwiki.api.task.model.TaskState
import cz.gregetom.graphwiki.task.dao.data.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, String> {

    fun findAllByAssigneeAndStateIsNot(assignee: String, state: TaskState): List<Task>

    fun findAllByAssigneeRoleInAndAssigneeIsNullAndStateIn(roles: Set<String>, vararg states: TaskState): List<Task>

    fun findAllByAssigneeRoleInAndStateIn(roles: Set<String>, vararg states: TaskState): List<Task>

    fun countAllByAssigneeAndStateIn(assignee: String, vararg states: TaskState): Long

    fun countAllByAssigneeRoleInAndStateIn(assigneeRole: Set<String>, vararg states: TaskState): Long
}
