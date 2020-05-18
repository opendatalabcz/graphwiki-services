package cz.gregetom.graphwiki.graph.web.history

import cz.gregetom.graphwiki.api.graph.model.HistoryTO
import cz.gregetom.graphwiki.api.graph.model.HistoryType
import cz.gregetom.graphwiki.api.graph.model.LinkTO
import cz.gregetom.graphwiki.graph.dao.jpa.data.History
import cz.gregetom.graphwiki.graph.web.ComplaintApiLinks
import cz.gregetom.graphwiki.graph.web.EntityRequestApiLinks
import cz.gregetom.graphwiki.graph.web.LinkFactory
import cz.gregetom.graphwiki.graph.web.toLinkTO
import org.springframework.stereotype.Component

@Component
class HistoryMapper(private val linkFactory: LinkFactory) {

    fun map(history: History): HistoryTO {
        return HistoryTO(
                id = history.id,
                author = linkFactory.userSelf(history.author, "author").toLinkTO(),
                created = history.created,
                type = history.type,
                relatedEntity = history.relatedEntityLink(),
                previousState = history.previousState,
                currentState = history.currentState
        )
    }
}

private fun History.relatedEntityLink(): LinkTO? {
    return when (type) {
        HistoryType.ENTITY_REQUEST_CREATED,
        HistoryType.ENTITY_REQUEST_APPROVED,
        HistoryType.ENTITY_REQUEST_REJECTED -> relatedEntityId!!.let { EntityRequestApiLinks.self(relatedEntityId).toLinkTO() }
        HistoryType.COMPLAINT_CREATED,
        HistoryType.COMPLAINT_APPROVED,
        HistoryType.COMPLAINT_REJECTED -> relatedEntityId!!.let { ComplaintApiLinks.self(relatedEntityId).toLinkTO() }
        else -> null
    }
}
