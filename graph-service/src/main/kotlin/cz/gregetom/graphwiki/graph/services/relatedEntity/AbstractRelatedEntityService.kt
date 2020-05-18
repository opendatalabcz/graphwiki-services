package cz.gregetom.graphwiki.graph.services.relatedEntity

import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.commons.web.InterCommunicationRestTemplate
import cz.gregetom.graphwiki.graph.dao.jpa.data.RelatedEntity
import cz.gregetom.graphwiki.graph.dao.jpa.repository.RelatedEntityRepository
import cz.gregetom.graphwiki.graph.services.graphEntity.GraphEntityServiceProvider
import cz.gregetom.graphwiki.graph.services.history.HistoryService
import cz.gregetom.graphwiki.graph.web.LinkFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder

abstract class AbstractRelatedEntityService<T : RelatedEntity<U>, U>(
        private val repository: RelatedEntityRepository<T, U>,
        private val entityActiveStates: Array<U>,
        private val entityFinalStates: Array<U>,
        private val entityModifyState: U
) {

    @Autowired
    protected lateinit var linkFactory: LinkFactory
    @Autowired
    protected lateinit var restTemplate: InterCommunicationRestTemplate
    @Autowired
    protected lateinit var graphEntityServiceProvider: GraphEntityServiceProvider
    @Autowired
    protected lateinit var historyService: HistoryService


    /**
     * Find related entity by id.
     *
     * @param id related entity id
     * @return related entity
     */
    fun findById(id: String): T {
        return repository.getOne(id)
    }

    /**
     * Change state of related entity.
     * Task is finished when state is final and task is still active.
     *
     * @param id related entity id
     * @param nextState new state
     * @param force indicates if assignee will be checked or not
     * @throws ResponseStatusException if force is false and current user is not assignee
     * @return related entity
     */
    fun stateTransition(id: String, nextState: U, force: Boolean = false) {
        LOGGER.info("Change state of related entity $id, new state: $nextState")
        val entity = repository.getOne(id)
        if (!force && UserAccessor.currentUserIsNot(entity.assignee)) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot move related entity $id to state $nextState, you are not assignee.")
        }

        val updatedEntity = repository.save(entity.apply { this.state = nextState })

        if (entity.taskUrl !== null && entityFinalStates.contains(nextState)) {
            this.finishTask(entity)
        }

        this.afterMoveToState(updatedEntity, nextState)
    }

    /**
     * Invoked after related entity state is changed.
     *
     * @param entity related entity
     * @param state new state
     */
    protected open fun afterMoveToState(entity: T, state: U) {
        // override if you want
    }

    /**
     * Assign related entity.
     *
     * @param id related entity id
     * @param assignee target assignee, or null for unassignment
     * @throws ResponseStatusException if current user is not assignee or related entity is in final state
     */
    fun assign(id: String, assignee: String?) {
        LOGGER.info("Assign related entity $id to $assignee")
        val entity = repository.getOne(id)
        if (assignee !== null && entity.assignee !== null) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot assign, entity already assigned.")
        }
        if (entityFinalStates.contains(entity.state)) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot assign, entity is in final state.")
        }
        repository.save(entity.apply { this.assignee = assignee })
    }

    /**
     * Invalidate all related entities when graph entity state is changed.
     *
     * @param graphEntityId graph entity entity
     */
    fun relatedGraphEntityStateChange(graphEntityId: String) {
        repository.findAllByEntityIdAndStateIsIn(graphEntityId, *entityActiveStates)
                .forEach { this.stateTransition(it.id, entityModifyState, true) }
    }

    /**
     * Finish active task for related entity.
     *
     * @param entity related entity
     */
    protected fun finishTask(entity: T) {
        LOGGER.info("Finish related entity ${entity.id} task (${entity.taskUrl})")
        restTemplate.deleteAsTechnicalUser(
                UriComponentsBuilder.fromUri(entity.taskUrl!!).queryParam("userId", UserAccessor.currentUserIdOrThrow).build().toUri()
        )
        repository.save(entity.apply { this.taskUrl = null })
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AbstractRelatedEntityService::class.java)
    }
}
