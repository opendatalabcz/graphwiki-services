package cz.gregetom.graphwiki.graph.services.relatedEntity

import cz.gregetom.graphwiki.api.graph.model.*
import cz.gregetom.graphwiki.api.task.model.CreateTaskTO
import cz.gregetom.graphwiki.api.task.model.TaskType
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.graph.dao.jpa.data.Complaint
import cz.gregetom.graphwiki.graph.dao.jpa.repository.ComplaintRepository
import cz.gregetom.graphwiki.graph.web.ComplaintApiLinks
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime
import java.util.*

@Service
class ComplaintService(private val complaintRepository: ComplaintRepository) :
        AbstractRelatedEntityService<Complaint,
                ComplaintState>(complaintRepository, ACTIVE_STATES, FINAL_STATES, ComplaintState.ENTITY_MODIFIED) {

    /***
     * Create complaint for related graph entity.
     * New task in task-service is created.
     * History records is added to related graph entity.
     *
     * @param entityId related graph entity id
     * @param createComplaintTO new complaint value
     * @param entityType graph entity type
     * @throws ResponseStatusException if complaint is not active
     * @return created complaint
     */
    fun create(entityId: String, createComplaintTO: CreateComplaintTO, entityType: GraphEntityType): String {
        LOGGER.info("Create new complaint for $entityType $entityId, $createComplaintTO")
        val graphEntity = graphEntityServiceProvider.getServiceForType(entityType).findById(entityId)
                .apply { if (this.state !== GraphEntityState.ACTIVE) throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot create complaint, graph entity is not active.") }

        val complaint = complaintRepository.save(
                Complaint(
                        id = UUID.randomUUID().toString(),
                        entityId = entityId,
                        entityType = entityType,
                        author = UserAccessor.currentUserIdOrThrow,
                        title = createComplaintTO.title,
                        explanation = createComplaintTO.explanation,
                        state = ComplaintState.ADMIN_DECISION,
                        created = OffsetDateTime.now()
                )
        )

        LOGGER.info("Create task for complaint ${complaint.id}")
        val createTaskTO = CreateTaskTO(
                type = TaskType.COMPLAINT,
                entityId = complaint.id,
                entityLabel = graphEntity.label(),
                entityUrl = ComplaintApiLinks.self(complaint.id).toUri(),
                assignUrl = ComplaintApiLinks.assign(complaint.id).toUri(),
                author = complaint.author,
                created = complaint.created,
                assigneeRole = Roles.ROLE_ADMIN
        )
        val taskUrl = restTemplate.postForLocationAsTechnicalUser(linkFactory.taskCreate().toUri(), createTaskTO)
        complaintRepository.save(complaint.copy(taskUrl = taskUrl))

        historyService.saveRelatedEntityHistory(
                entityId = entityId,
                author = complaint.author,
                type = HistoryType.COMPLAINT_CREATED,
                relatedEntityId = complaint.id
        )
        return complaint.id
    }

    /**
     * Find current related complaints for graph entity.
     *
     * @param entityId graph entity id
     * @return list of related complaints
     */
    fun findCurrentRelatedByEntityId(entityId: String): List<Complaint> {
        return complaintRepository.findAllByEntityIdAndStateIsIn(entityId, *ACTIVE_STATES)
    }

    /**
     * Find historic related complaints for graph entity.
     *
     * @param entityId graph entity id
     * @return list of related complaints
     */
    fun findHistoricRelatedByEntityId(entityId: String): List<Complaint> {
        return complaintRepository.findAllByEntityIdAndStateIsIn(entityId, *FINAL_STATES)
    }

    /**
     * Find current related complaints for complaint.
     *
     * @param entityId complaint id
     * @return list of related complaints
     */
    fun findCurrentRelatedByComplaintId(id: String): List<Complaint> {
        return findCurrentRelatedByEntityId(complaintRepository.getOne(id).entityId).filter { it.id != id }
    }

    /**
     * Find historic related complaints for complaint.
     *
     * @param entityId complaint id
     * @return list of related complaints
     */
    fun findHistoricRelatedByComplaintId(id: String): List<Complaint> {
        return findHistoricRelatedByEntityId(complaintRepository.getOne(id).entityId).filter { it.id != id }
    }

    override fun afterMoveToState(entity: Complaint, state: ComplaintState) {
        when (state) {
            ComplaintState.APPROVED -> onComplaintApproved(entity)
            ComplaintState.REJECTED -> onComplaintRejected(entity)
            else -> {
                // do nothing
            }
        }
    }

    private fun onComplaintApproved(entity: Complaint) {
        LOGGER.info("Complaint approved, change state of graph entity ${entity.entityType} ${entity.entityId} and other related entities")
        val (previousState, currentState) =
                graphEntityServiceProvider.getServiceForType(entity.entityType).moveToState(entity.entityId, GraphEntityState.REVOKED)

        historyService.saveRelatedEntityHistory(
                entityId = entity.entityId,
                author = UserAccessor.currentUserIdOrThrow,
                type = HistoryType.COMPLAINT_APPROVED,
                relatedEntityId = entity.id,
                previousState = previousState,
                currentState = currentState
        )

        complaintRepository.findAllByEntityIdAndStateIsIn(entity.entityId, *ACTIVE_STATES)
                .minus(entity)
                .forEach { this.stateTransition(it.id, ComplaintState.ENTITY_MODIFIED) }
    }

    private fun onComplaintRejected(entity: Complaint) {
        historyService.saveRelatedEntityHistory(
                entityId = entity.entityId,
                author = UserAccessor.currentUserIdOrThrow,
                type = HistoryType.COMPLAINT_REJECTED,
                relatedEntityId = entity.id
        )
    }

    companion object {
        private val ACTIVE_STATES = setOf(ComplaintState.ADMIN_DECISION).toTypedArray()
        private val FINAL_STATES = setOf(ComplaintState.APPROVED, ComplaintState.REJECTED, ComplaintState.ENTITY_MODIFIED).toTypedArray()

        private val LOGGER = LoggerFactory.getLogger(ComplaintService::class.java)
    }
}
