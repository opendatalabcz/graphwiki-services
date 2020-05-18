package cz.gregetom.graphwiki.graph.web.graphEntity.relationship

import cz.gregetom.graphwiki.api.graph.api.RelationshipApi
import cz.gregetom.graphwiki.api.graph.model.CreateRelationshipTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.RelatedRelationshipsTO
import cz.gregetom.graphwiki.api.graph.model.RelationshipTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.graph.services.graphEntity.RelationshipService
import cz.gregetom.graphwiki.graph.web.EntityRequestApiLinks
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
class RelationshipController(
        private val relationshipService: RelationshipService,
        private val relationshipMapper: RelationshipMapper
) : RelationshipApi {

    @Transactional(readOnly = true)
    override fun findRelationshipById(@Size(max = 50) @PathVariable id: String): ResponseEntity<RelationshipTO> {
        return ResponseEntity.ok(relationshipMapper.map(relationshipService.findById(id)))
    }

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional
    override fun createRelationship(@Valid @RequestBody createRelationshipTO: CreateRelationshipTO): ResponseEntity<Unit> {
        val entityRequestId = relationshipService.create(createRelationshipTO)
        return ResponseEntity
                .created(EntityRequestApiLinks.self(entityRequestId).toUri())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION)
                .build()
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun updateRelationship(@Size(max = 50) @PathVariable id: String,
                                    @Valid @RequestBody createRelationshipTO: CreateRelationshipTO): ResponseEntity<Unit> {
        return ResponseEntity.ok(relationshipService.update(id, createRelationshipTO))
    }

    @Transactional(readOnly = true)
    override fun findRelatedRelationshipsForVertex(@NotNull @Size(max = 50) @RequestParam vertexId: String): ResponseEntity<RelatedRelationshipsTO> {
        return ResponseEntity.ok(
                RelatedRelationshipsTO(
                        incoming = relationshipService.findIncomingForVertex(vertexId).map { relationshipMapper.map(it) },
                        outgoing = relationshipService.findOutgoingForVertex(vertexId).map { relationshipMapper.map(it) }
                )
        )
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun deleteRelationship(@Size(max = 50) @PathVariable id: String): ResponseEntity<Unit> {
        return ResponseEntity.ok(relationshipService.delete(id))
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun relationshipStateTransition(@Size(max = 50) @PathVariable id: String,
                                             @NotNull @RequestParam nextState: GraphEntityState): ResponseEntity<Unit> {
        relationshipService.moveToState(id, nextState, true)
        return ResponseEntity.ok().build()
    }
}
