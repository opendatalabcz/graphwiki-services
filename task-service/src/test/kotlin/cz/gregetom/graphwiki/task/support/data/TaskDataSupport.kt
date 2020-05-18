package cz.gregetom.graphwiki.task.support.data

import cz.gregetom.graphwiki.api.task.model.CreateTaskTO
import cz.gregetom.graphwiki.api.task.model.TaskState
import cz.gregetom.graphwiki.api.task.model.TaskType
import cz.gregetom.graphwiki.commons.test.RandomGenerator
import cz.gregetom.graphwiki.task.dao.data.Task
import cz.gregetom.graphwiki.task.dao.repository.TaskRepository
import org.springframework.stereotype.Component
import java.net.URI
import java.time.OffsetDateTime
import java.util.*

@Component
class TaskDataSupport(private val taskRepository: TaskRepository) {

    private val randomGenerator = RandomGenerator.instance

    fun randomCreateTaskTO(assigneeRole: String): CreateTaskTO {
        return CreateTaskTO(
                type = randomGenerator.nextObject(TaskType::class.java),
                entityId = RandomGenerator.randomString(50),
                entityLabel = randomGenerator.nextObject(String::class.java),
                entityUrl = URI("http://test-entity.cz"),
                assignUrl = URI("http://test-assign.cz"),
                assigneeRole = assigneeRole,
                author = randomGenerator.nextObject(String::class.java),
                created = randomGenerator.nextObject(OffsetDateTime::class.java)
        )
    }

    fun createTask(assignee: String? = null, assigneeRole: String? = null, state: TaskState): Task {
        return taskRepository.save(
                RandomGenerator.instance.nextObject(Task::class.java).copy(
                        id = UUID.randomUUID().toString(),
                        assignee = assignee,
                        assigneeRole = assigneeRole,
                        state = state
                )
        )
    }
}
