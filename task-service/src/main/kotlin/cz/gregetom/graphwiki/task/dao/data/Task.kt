package cz.gregetom.graphwiki.task.dao.data

import cz.gregetom.graphwiki.api.task.model.TaskState
import cz.gregetom.graphwiki.api.task.model.TaskType
import java.net.URI
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "TASK")
data class Task(
        @Id
        val id: String,
        @Enumerated(EnumType.STRING)
        val type: TaskType,
        val author: String,
        val created: OffsetDateTime,
        val entityId: String,
        val entityLabel: String,
        val entityUrl: URI,
        val assignUrl: URI,
        @Enumerated(EnumType.STRING)
        val state: TaskState,
        val assignee: String? = null,
        val assigneeRole: String?
)
