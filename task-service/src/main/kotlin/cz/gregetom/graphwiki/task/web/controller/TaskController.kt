package cz.gregetom.graphwiki.task.web.controller

import cz.gregetom.graphwiki.api.task.api.TaskApi
import cz.gregetom.graphwiki.api.task.model.CreateTaskTO
import cz.gregetom.graphwiki.api.task.model.TaskTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.task.services.TaskService
import cz.gregetom.graphwiki.task.web.TaskApiLinks
import cz.gregetom.graphwiki.task.web.mapper.TaskMapper
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping
@Validated
class TaskController(
        private val taskMapper: TaskMapper,
        private val taskService: TaskService
) : TaskApi {

    @Secured(Roles.ROLE_TECHNICAL)
    @Transactional
    override fun create(@Valid @RequestBody createTaskTO: CreateTaskTO): ResponseEntity<Unit> {
        val task = taskService.save(createTaskTO)
        return ResponseEntity
                .created(TaskApiLinks.self(task.id).toUri())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION)
                .build()
    }

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional(readOnly = true)
    override fun privateInbox(): ResponseEntity<List<TaskTO>> {
        return ResponseEntity.ok(taskService.findAllAssignedToUser().map { taskMapper.map(it) })
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional(readOnly = true)
    override fun teamInbox(@NotNull @RequestParam(required = false) showAssigned: Boolean): ResponseEntity<List<TaskTO>> {
        return ResponseEntity.ok(taskService.findAllForUserRole(showAssigned).map { taskMapper.map(it) })
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun assign(@Size(max = 50) @PathVariable id: String,
                        @Size(max = 50) @RequestParam(required = false) assignee: String?): ResponseEntity<Unit> {
        return ResponseEntity.ok(taskService.assign(id, assignee))
    }

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional(readOnly = true)
    override fun findById(@Size(max = 50) @PathVariable id: String): ResponseEntity<TaskTO> {
        return ResponseEntity.ok(taskMapper.map(taskService.findById(id)))
    }

    @Secured(Roles.ROLE_TECHNICAL)
    @Transactional
    override fun finish(@Size(max = 50) @PathVariable id: String,
                        @NotNull @Size(max = 50) @RequestParam userId: String): ResponseEntity<Unit> {
        return ResponseEntity.ok(taskService.finish(id, userId))
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional(readOnly = true)
    override fun openTaskCount(): ResponseEntity<Long> {
        return ResponseEntity.ok(taskService.openTaskCount())
    }
}
