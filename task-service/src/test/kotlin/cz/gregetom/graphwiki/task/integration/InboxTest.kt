package cz.gregetom.graphwiki.task.integration

import cz.gregetom.graphwiki.api.task.model.TaskState
import cz.gregetom.graphwiki.api.task.model.TaskTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.task.support.data.TaskDataSupport
import cz.gregetom.graphwiki.task.web.TaskApiLinks
import cz.gregetom.graphwiki.user.AbstractIntegrationTest
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class InboxTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var taskDataSupport: TaskDataSupport

    @Test
    fun teamInboxTest() {
        val user = TestUsers.ADMIN

        val openTask1 = taskDataSupport.createTask(assigneeRole = Roles.ROLE_ADMIN, state = TaskState.OPEN)
        val openTask2 = taskDataSupport.createTask(assigneeRole = Roles.ROLE_ADMIN, state = TaskState.OPEN)
        val assignedTask = taskDataSupport.createTask(user.username, Roles.ROLE_ADMIN, TaskState.ASSIGNED)
        val assignedTaskToAnotherUser = taskDataSupport.createTask(UUID.randomUUID().toString(), Roles.ROLE_ADMIN, TaskState.ASSIGNED)
        // doneTask
        taskDataSupport.createTask(assigneeRole = Roles.ROLE_ADMIN, state = TaskState.DONE)


        val teamInbox = httpGet.doGet(Array<TaskTO>::class, TaskApiLinks.teamInbox(false).toUri(), user)
        assertThat(teamInbox.toList()).asList().hasSize(2)
        assertThat(teamInbox).isSortedAccordingTo(
                Comparator<TaskTO> { o1, o2 -> o1.created.compareTo(o2.created) }.reversed()
        )
        teamInbox.zip(listOf(openTask1, openTask2).sortedByDescending { it.created }).forEach {
            assertThat(it.first).isEqualToComparingOnlyGivenFields(it.second, "created", "state", "type")
            assertThat(it.first.author).isNotNull
            assertThat(it.first.assignee).isNull()
            assertThat(it.first.links.entity).isNotNull
            assertThat(it.first.links.assign).isNotNull
        }

        val teamInboxWithAssigned = httpGet.doGet(Array<TaskTO>::class, TaskApiLinks.teamInbox(true).toUri(), user)
        assertThat(teamInboxWithAssigned.toList()).asList().hasSize(4)
        assertThat(teamInbox).isSortedAccordingTo(
                Comparator<TaskTO> { o1, o2 -> o1.created.compareTo(o2.created) }.reversed()
        )
        teamInboxWithAssigned.zip(listOf(openTask1, openTask2, assignedTask, assignedTaskToAnotherUser).sortedByDescending { it.created }).forEach { pair ->
            assertThat(pair.first).isEqualToComparingOnlyGivenFields(pair.second, "created", "state", "type")
            assertThat(pair.first.links.entity).isNotNull
            pair.first.assignee?.let {
                assertThat(pair.first.links.assign).isNull()
            } ?: assertThat(pair.first.links.assign).isNotNull
        }

        assertThat(httpGet.doGet(Long::class, TaskApiLinks.openTaskCount().toUri())).isEqualTo(3)
    }


    @Test
    fun privateInboxTest() {
        val user = TestUsers.ADMIN

        val assignedTask1 = taskDataSupport.createTask(user.username, Roles.ROLE_ADMIN, TaskState.ASSIGNED)
        val assignedTask2 = taskDataSupport.createTask(user.username, Roles.ROLE_ADMIN, TaskState.ASSIGNED)
        // assignedDoneTask
        taskDataSupport.createTask(user.username, Roles.ROLE_ADMIN, TaskState.DONE)
        // openTask
        taskDataSupport.createTask(assigneeRole = Roles.ROLE_ADMIN, state = TaskState.OPEN)
        // assignedTaskToAnotherUser
        taskDataSupport.createTask(TestUsers.ADMIN_ANOTHER.username, Roles.ROLE_ADMIN, TaskState.ASSIGNED)

        val privateInbox = httpGet.doGet(Array<TaskTO>::class, TaskApiLinks.privateInbox().toUri(), user)
        assertThat(privateInbox.toList()).asList().hasSize(2)
        assertThat(privateInbox).isSortedAccordingTo(
                Comparator<TaskTO> { o1, o2 -> o1.created.compareTo(o2.created) }.reversed()
        )
        privateInbox.zip(listOf(assignedTask1, assignedTask2).sortedByDescending { it.created }).forEach { pair ->
            assertThat(pair.first).isEqualToComparingOnlyGivenFields(pair.second, "created", "state", "type")
            assertThat(pair.first.assignee).isNotNull
            assertThat(pair.first.links.entity).isNotNull
            assertThat(pair.first.links.assign).isNull()
        }
    }
}
