package cz.gregetom.graphwiki.task.integration

import cz.gregetom.graphwiki.api.task.model.TaskState
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.test.RandomGenerator
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.task.dao.repository.TaskRepository
import cz.gregetom.graphwiki.task.support.data.TaskDataSupport
import cz.gregetom.graphwiki.task.web.controller.TaskController
import cz.gregetom.graphwiki.user.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus

class FinishingTaskTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var taskRepository: TaskRepository
    @Autowired
    private lateinit var taskDataSupport: TaskDataSupport

    @Test
    fun taskFinishingTest() {
        val assigneeId = RandomGenerator.randomString(50)
        val assignedTask = taskDataSupport.createTask(assigneeRole = Roles.ROLE_ADMIN, state = TaskState.ASSIGNED, assignee = assigneeId)
        httpDelete.doDelete(linkTo(methodOn(TaskController::class.java).finish(assignedTask.id, assigneeId)).withRel("finish").toUri(), TestUsers.TECHNICAL)

        val deletedTask = taskRepository.getOne(assignedTask.id)
        assertThat(deletedTask.state).isEqualTo(TaskState.DONE)
    }

    @Test
    fun taskFinishingAlreadyDoneTest() {
        val assigneeId = RandomGenerator.randomString(50)
        val assignedTask = taskDataSupport.createTask(assigneeRole = Roles.ROLE_ADMIN, state = TaskState.DONE, assignee = assigneeId)
        httpDelete.doDeleteAndExpect(
                linkTo(methodOn(TaskController::class.java).finish(assignedTask.id, assigneeId)).withRel("finish").toUri(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                TestUsers.TECHNICAL
        )
    }

    @Test
    fun taskFinishingNotAssigneeTest() {
        val assigneeId = RandomGenerator.randomString(50)
        val assignedTask = taskDataSupport.createTask(assigneeRole = Roles.ROLE_ADMIN, state = TaskState.ASSIGNED, assignee = assigneeId)

        val anotherUserId = RandomGenerator.randomString(50)

        httpDelete.doDeleteAndExpect(
                linkTo(methodOn(TaskController::class.java).finish(assignedTask.id, anotherUserId)).withRel("finish").toUri(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                TestUsers.TECHNICAL
        )
    }
}
