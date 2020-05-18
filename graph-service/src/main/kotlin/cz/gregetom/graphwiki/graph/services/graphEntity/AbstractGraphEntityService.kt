package cz.gregetom.graphwiki.graph.services.graphEntity

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.commons.web.InterCommunicationRestTemplate
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphEntity
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.GraphEntityRepository
import cz.gregetom.graphwiki.graph.dao.jpa.data.RelatedEntity
import cz.gregetom.graphwiki.graph.services.history.HistoryService
import cz.gregetom.graphwiki.graph.services.relatedEntity.AbstractRelatedEntityService
import cz.gregetom.graphwiki.graph.services.relatedEntity.EntityRequestService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractGraphEntityService<T : GraphEntity>(
        private val graphEntityType: GraphEntityType,
        private val repository: GraphEntityRepository<T>
) {
    @Autowired
    private lateinit var entityRequestService: EntityRequestService
    @Autowired
    private lateinit var relatedEntityServices: List<AbstractRelatedEntityService<out RelatedEntity<*>, *>>
    @Autowired
    private lateinit var historyService: HistoryService
    @Autowired
    private lateinit var restTemplate: InterCommunicationRestTemplate

    /**
     * Determine if current service supports [type] of graph entity.
     *
     * @param type graph entity type
     * @return true if type is supported, otherwise false
     */
    fun support(type: GraphEntityType): Boolean {
        return this.graphEntityType === type
    }

    fun findById(id: String): T {
        return repository.findById(id)
    }

    fun delete(id: String) {
        LOGGER.info("Delete graph entity $id")
        this.moveToState(id, GraphEntityState.DELETED, true)
    }

    /**
     * Change state of graph entity.
     * If next state is final, then notify all related entities.
     * Save record about state transition to history.
     *
     * @param id id of graph entity to be updated
     * @param nextState graph entity next state
     * @return previous and current state
     */
    fun moveToState(id: String, nextState: GraphEntityState, addHistoryRecord: Boolean = false): Pair<GraphEntityState, GraphEntityState> {
        LOGGER.info("Change state of graph entity $id, new state: $nextState")
        val entity = repository.findById(id)
        val previousState = entity.state
        val updated = repository.update(entity.apply { this.state = nextState })

        if (FINAL_STATES.contains(nextState)) {
            LOGGER.info("New state is final, notify related entities")
            relatedEntityServices.forEach { it.relatedGraphEntityStateChange(id) }
        }
        if (addHistoryRecord) {
            historyService.saveStateTransition(id, UserAccessor.currentUserIdOrThrow, previousState, updated.state)
        }
        return Pair(previousState, updated.state)
    }

    /**
     * Create entity request after graph entity is created.
     *
     * @param graphEntity created graph entity
     * @return id of created entity request
     */
    protected fun createRelatedEntityRequest(graphEntity: GraphEntity): String {
        LOGGER.info("Create related entity request for graph entity ${graphEntity.id}")
        return entityRequestService.create(graphEntity, graphEntityType).id
    }

    companion object {
        private val FINAL_STATES = setOf(GraphEntityState.DELETED, GraphEntityState.REVOKED, GraphEntityState.REJECTED)

        private val LOGGER = LoggerFactory.getLogger(AbstractGraphEntityService::class.java)
    }
}
