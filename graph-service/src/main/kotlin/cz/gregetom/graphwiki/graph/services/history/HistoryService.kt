package cz.gregetom.graphwiki.graph.services.history

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.HistoryType
import cz.gregetom.graphwiki.graph.dao.jpa.data.History
import cz.gregetom.graphwiki.graph.dao.jpa.repository.HistoryRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

@Service
class HistoryService(private val repositoryHistory: HistoryRepository) {

    fun findAllByEntityId(entityId: String): List<History> {
        return repositoryHistory.findAllByEntityId(entityId).sortedByDescending { it.created }
    }

    /**
     * Save history record about related entity creation.
     */
    fun saveRelatedEntityHistory(entityId: String,
                                 author: String,
                                 type: HistoryType,
                                 relatedEntityId: String,
                                 previousState: GraphEntityState? = null,
                                 currentState: GraphEntityState? = null): String {
        LOGGER.info("Save $type history record about related entity $relatedEntityId for $entityId")
        return repositoryHistory.save(
                History(
                        id = UUID.randomUUID().toString(),
                        author = author,
                        created = OffsetDateTime.now(),
                        entityId = entityId,
                        type = type,
                        relatedEntityId = relatedEntityId,
                        previousState = previousState,
                        currentState = currentState
                )
        ).id
    }

    /**
     * Save history record about state transition.
     */
    fun saveStateTransition(entityId: String,
                            author: String,
                            previousState: GraphEntityState?,
                            currentState: GraphEntityState): String {
        LOGGER.info("Save state transition record for $entityId, $previousState -> $currentState")
        return repositoryHistory.save(
                History(
                        id = UUID.randomUUID().toString(),
                        author = author,
                        created = OffsetDateTime.now(),
                        type = HistoryType.STATE_TRANSITION,
                        entityId = entityId,
                        previousState = previousState,
                        currentState = currentState
                )
        ).id
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(HistoryService::class.java)
    }
}
