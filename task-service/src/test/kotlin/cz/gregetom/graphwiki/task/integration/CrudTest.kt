package cz.gregetom.graphwiki.task.integration

import cz.gregetom.graphwiki.api.task.model.TaskTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
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

class CrudTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var taskRepository: TaskRepository
    @Autowired
    private lateinit var taskDataSupport: TaskDataSupport

    @Test
    fun taskCrudTest() {
        val createTaskTO = taskDataSupport.randomCreateTaskTO(Roles.ROLE_ADMIN)

        val location = httpPost.doPost(
                linkTo(methodOn(TaskController::class.java).create(createTaskTO)).withRel("create").toUri(), createTaskTO, TestUsers.TECHNICAL)

        val createdTask = taskRepository.findAll().first()

        assertThat(createTaskTO).isEqualToComparingOnlyGivenFields(createdTask,
                "type", "entityId", "entityLabel", "entityUrl", "assignUrl", "assigneeRole", "author", "created")


        val taskForAdmin = httpGet.doGet(TaskTO::class, location)
        assertThat(taskForAdmin.links.assign).isNotNull
        assertThat(taskForAdmin.links.unassign).isNull()
        assertThat(taskForAdmin.author).isNotNull
        assertThat(taskForAdmin.label).isEqualTo(createdTask.entityLabel)
        assertThat(taskForAdmin).isEqualToComparingOnlyGivenFields(createdTask,
                "id", "created", "state", "type", "assignee")

        val taskForAnotherAdmin = httpGet.doGet(TaskTO::class, location, TestUsers.ADMIN_ANOTHER)
        assertThat(taskForAnotherAdmin.links.assign).isNotNull
        assertThat(taskForAnotherAdmin.links.unassign).isNull()
    }
}
