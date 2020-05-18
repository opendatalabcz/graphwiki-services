package cz.gregetom.graphwiki.task.web.controller

import cz.gregetom.graphwiki.api.task.api.ApplicationEntryApi
import cz.gregetom.graphwiki.api.task.model.ApplicationEntryActions
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.task.web.TaskApiLinks
import cz.gregetom.graphwiki.task.web.toLinkTO
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
@Validated
class ApplicationEntryController : ApplicationEntryApi {

    override fun getEntryActions(): ResponseEntity<ApplicationEntryActions> {
        return ResponseEntity.ok(
                ApplicationEntryActions(
                        taskInboxTeam = if (UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) TaskApiLinks.teamInbox(false).toLinkTO() else null,
                        taskInboxTeamWithAssigned = if (UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) TaskApiLinks.teamInbox(true).toLinkTO() else null,
                        taskInboxPrivate = if (UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) TaskApiLinks.privateInbox().toLinkTO() else null,
                        openTaskCount = if (UserAccessor.hasRoleAny(Roles.ROLE_ADMIN)) TaskApiLinks.openTaskCount().toLinkTO() else null
                )
        )
    }
}
