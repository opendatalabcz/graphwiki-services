package cz.gregetom.graphwiki.graph.web.relatedEntity.entityRequest

import cz.gregetom.graphwiki.api.graph.api.EntityRequestApi
import cz.gregetom.graphwiki.api.graph.model.EntityRequestState
import cz.gregetom.graphwiki.api.graph.model.EntityRequestTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.graph.services.relatedEntity.EntityRequestService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping
@Validated
class EntityRequestController(
        private val entityRequestService: EntityRequestService,
        private val entityRequestMapper: EntityRequestMapper
) : EntityRequestApi {

    @Transactional(readOnly = true)
    override fun findEntityRequestById(@Size(max = 50) @PathVariable id: String): ResponseEntity<EntityRequestTO> {
        return ResponseEntity.ok(entityRequestMapper.map(entityRequestService.findById(id)))
    }

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional
    override fun entityRequestStateTransition(@Size(max = 50) @PathVariable id: String,
                                              @NotNull @RequestParam nextState: EntityRequestState): ResponseEntity<Unit> {
        return ResponseEntity.ok(entityRequestService.stateTransition(id, nextState))
    }

    @Secured(Roles.ROLE_TECHNICAL)
    @Transactional
    override fun assignEntityRequest(@Size(max = 50) @PathVariable id: String,
                                     @Size(max = 50) @RequestParam(required = false) assignee: String?): ResponseEntity<Unit> {
        return ResponseEntity.ok(entityRequestService.assign(id, assignee))
    }
}
