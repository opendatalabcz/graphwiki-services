package cz.gregetom.graphwiki.task.integration

import cz.gregetom.graphwiki.api.task.model.TaskState
import cz.gregetom.graphwiki.api.task.model.TaskTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.test.RandomGenerator
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.task.dao.repository.TaskRepository
import cz.gregetom.graphwiki.task.support.data.TaskDataSupport
import cz.gregetom.graphwiki.task.web.TaskApiLinks
import cz.gregetom.graphwiki.user.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators

class AssignmentTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var taskRepository: TaskRepository
    @Autowired
    private lateinit var taskDataSupport: TaskDataSupport

    @Test
    fun taskAssignmentTest() {
        val user = TestUsers.ADMIN
        val task = taskDataSupport.createTask(assigneeRole = Roles.ROLE_ADMIN, state = TaskState.OPEN)

        assertThat(httpGet.doGet(Array<TaskTO>::class, TaskApiLinks.privateInbox().toUri(), user).toList()).asList().isEmpty()
        assertThat(httpGet.doGet(Array<TaskTO>::class, TaskApiLinks.teamInbox(false).toUri(), user).toList()).asList().hasSize(1)

        mockServer.expect(ExpectedCount.once(),
                MockRestRequestMatchers.requestTo(Matchers.endsWith("?assignee=${user.username}")))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK))

        httpPut.doPutEmptyBody(TaskApiLinks.taskAssign(task.id, user.username).toUri())
        assertThat(taskRepository.getOne(task.id).state).isEqualTo(TaskState.ASSIGNED)

        val actual = httpGet.doGet(TaskTO::class, TaskApiLinks.self(task.id).toUri())
        assertThat(actual.links.assign).isNull()
        assertThat(actual.links.unassign).isNotNull

        mockServer.verify()
        mockServer.reset()
        mockServer.expect(ExpectedCount.once(),
                MockRestRequestMatchers.requestTo(task.assignUrl.toString()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK))
        httpPut.doPutEmptyBody(TaskApiLinks.taskAssign(task.id, null).toUri())
        assertThat(taskRepository.getOne(task.id).state).isEqualTo(TaskState.OPEN)
    }

    @Test
    fun taskAssignmentFinishedTest() {
        val finishedTask = taskDataSupport.createTask(assigneeRole = Roles.ROLE_ADMIN, state = TaskState.DONE)
        httpPut.doPutAndExpect(TaskApiLinks.taskAssign(finishedTask.id, TestUsers.ADMIN_ANOTHER.username).toUri(), HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun taskAssignmentAlreadyAssignedTest() {
        val assignedTask = taskDataSupport.createTask(assigneeRole = Roles.ROLE_ADMIN, assignee = RandomGenerator.randomString(50), state = TaskState.ASSIGNED)
        httpPut.doPutAndExpect(TaskApiLinks.taskAssign(assignedTask.id, RandomGenerator.randomString(50)).toUri(), HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
