package cz.gregetom.graphwiki.graph.services.relatedEntity

import cz.gregetom.graphwiki.api.graph.model.EntityRequestState
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.api.graph.model.HistoryType
import cz.gregetom.graphwiki.api.task.model.CreateTaskTO
import cz.gregetom.graphwiki.api.task.model.TaskType
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphEntity
import cz.gregetom.graphwiki.graph.dao.jpa.data.EntityRequest
import cz.gregetom.graphwiki.graph.dao.jpa.repository.EntityRequestRepository
import cz.gregetom.graphwiki.graph.web.EntityRequestApiLinks
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

@Service
class EntityRequestService(private val entityRequestRepository: EntityRequestRepository) : AbstractRelatedEntityService<EntityRequest,
        EntityRequestState>(entityRequestRepository, ACTIVE_STATES, FINAL_STATES, EntityRequestState.ENTITY_MODIFIED) {

    /***
     * Create entity request for related graph entity.
     * New task in task-service is created.
     * History records is added to related graph entity.
     *
     * @param graphEntity related graph entity
     * @param entityType graph entity type
     * @return created entity request
     */
    fun create(graphEntity: GraphEntity, entityType: GraphEntityType): EntityRequest {
        LOGGER.info("Create new entity request for $entityType ${graphEntity.id}")
        val entityRequest = entityRequestRepository.save(
                EntityRequest(
                        id = UUID.randomUUID().toString(),
                        author = UserAccessor.currentUserIdOrThrow,
                        created = OffsetDateTime.now(),
                        assignee = null,
                        entityId = graphEntity.id,
                        entityType = entityType,
                        state = EntityRequestState.NEW
                )
        )

        LOGGER.info("Create task for entity request ${entityRequest.id}")
        val createTaskTO = CreateTaskTO(
                type = TaskType.ENTITY_REQUEST,
                entityId = entityRequest.id,
                entityLabel = graphEntity.label(),
                entityUrl = EntityRequestApiLinks.self(entityRequest.id).toUri(),
                assignUrl = EntityRequestApiLinks.assign(entityRequest.id).toUri(),
                author = entityRequest.author,
                created = entityRequest.created,
                assigneeRole = Roles.ROLE_ADMIN
        )
        val taskUrl = restTemplate.postForLocationAsTechnicalUser(linkFactory.taskCreate().toUri(), createTaskTO)
        entityRequestRepository.save(entityRequest.copy(taskUrl = taskUrl))

        historyService.saveRelatedEntityHistory(
                entityId = graphEntity.id,
                author = graphEntity.author,
                type = HistoryType.ENTITY_REQUEST_CREATED,
                relatedEntityId = entityRequest.id
        )

        return entityRequest
    }

    override fun afterMoveToState(entity: EntityRequest, state: EntityRequestState) {
        resolveNextGraphEntityStateAndHistoryType(state)?.let {
            val (nextState, historyType) = it
            LOGGER.info("Entity request ${entity.id} state changed to $state, change state of ${entity.entityType} ${entity.entityId} to $nextState")
            val (previousState, currentState) =
                    graphEntityServiceProvider.getServiceForType(entity.entityType).moveToState(entity.entityId, nextState)

            historyService.saveRelatedEntityHistory(
                    entityId = entity.entityId,
                    author = UserAccessor.currentUserIdOrThrow,
                    type = historyType,
                    relatedEntityId = entity.id,
                    previousState = previousState,
                    currentState = currentState
            )
        }
    }

    private fun resolveNextGraphEntityStateAndHistoryType(state: EntityRequestState): Pair<GraphEntityState, HistoryType>? {
        return when (state) {
            EntityRequestState.APPROVED -> Pair(GraphEntityState.ACTIVE, HistoryType.ENTITY_REQUEST_APPROVED)
            EntityRequestState.REJECTED -> Pair(GraphEntityState.REJECTED, HistoryType.ENTITY_REQUEST_REJECTED)
            else -> null
        }
    }

    companion object {
        private val ACTIVE_STATES = setOf(EntityRequestState.NEW).toTypedArray()
        private val FINAL_STATES = setOf(EntityRequestState.APPROVED, EntityRequestState.REJECTED, EntityRequestState.ENTITY_MODIFIED).toTypedArray()

        private val LOGGER = LoggerFactory.getLogger(EntityRequestService::class.java)
    }
}
