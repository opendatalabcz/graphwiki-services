package cz.gregetom.graphwiki.graph.web.relatedEntity.complaint

import cz.gregetom.graphwiki.api.graph.api.ComplaintApi
import cz.gregetom.graphwiki.api.graph.model.*
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.graph.services.relatedEntity.ComplaintService
import cz.gregetom.graphwiki.graph.web.ComplaintApiLinks
import cz.gregetom.graphwiki.graph.web.GraphEntityApiLinks
import cz.gregetom.graphwiki.graph.web.toLinkTO
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
class ComplaintController(
        private val complaintMapper: ComplaintMapper,
        private val complaintService: ComplaintService
) : ComplaintApi {

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional(readOnly = true)
    override fun complaintSetup(@NotNull @Size(max = 50) @RequestParam entityId: String,
                                @NotNull @RequestParam entityType: GraphEntityType): ResponseEntity<ComplaintSetupTO> {
        return ResponseEntity.ok(ComplaintSetupTO(
                links = ComplaintSetupTOLinks(
                        entity = GraphEntityApiLinks.selfByType(entityId, entityType).toLinkTO(),
                        create = ComplaintApiLinks.create(entityId, entityType).toLinkTO(),
                        related = ComplaintApiLinks.entityRelated(entityId).toLinkTO()
                )
        ))
    }

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional
    override fun createComplaint(@NotNull @Size(max = 50) @RequestParam entityId: String,
                                 @NotNull @RequestParam entityType: GraphEntityType,
                                 @Valid @RequestBody createComplaintTO: CreateComplaintTO): ResponseEntity<Unit> {
        val id = complaintService.create(entityId, createComplaintTO, entityType)
        return ResponseEntity
                .created(ComplaintApiLinks.self(id).toUri())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION)
                .build()
    }

    @Transactional(readOnly = true)
    override fun findComplaintById(@Size(max = 50) @PathVariable id: String): ResponseEntity<ComplaintTO> {
        return ResponseEntity.ok(complaintMapper.map(complaintService.findById(id)))
    }

    @Transactional(readOnly = true)
    override fun findRelatedComplaintsByEntityId(@NotNull @Size(max = 50) @RequestParam entityId: String): ResponseEntity<RelatedComplaintsTO> {
        return ResponseEntity.ok(
                RelatedComplaintsTO(
                        current = complaintService.findCurrentRelatedByEntityId(entityId).map { complaintMapper.map(it) },
                        historic = complaintService.findHistoricRelatedByEntityId(entityId).map { complaintMapper.map(it) }
                )
        )
    }

    @Transactional(readOnly = true)
    override fun findRelatedComplaintsByComplaintId(@Size(max = 50) @PathVariable id: String): ResponseEntity<RelatedComplaintsTO> {
        return ResponseEntity.ok(
                RelatedComplaintsTO(
                        current = complaintService.findCurrentRelatedByComplaintId(id).map { complaintMapper.map(it) },
                        historic = complaintService.findHistoricRelatedByComplaintId(id).map { complaintMapper.map(it) }
                )
        )
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun complaintStateTransition(@Size(max = 50) @PathVariable id: String,
                                          @NotNull @RequestParam nextState: ComplaintState): ResponseEntity<Unit> {
        return ResponseEntity.ok(complaintService.stateTransition(id, nextState))
    }

    @Secured(Roles.ROLE_TECHNICAL)
    @Transactional
    override fun assignComplaint(@Size(max = 50) @PathVariable id: String,
                                 @Size(max = 50) @RequestParam(required = false) assignee: String?): ResponseEntity<Unit> {
        return ResponseEntity.ok(complaintService.assign(id, assignee))
    }
}
