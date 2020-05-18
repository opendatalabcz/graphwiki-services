package cz.gregetom.graphwiki.graph.support.data

import cz.gregetom.graphwiki.graph.dao.jpa.data.History
import cz.gregetom.graphwiki.graph.dao.jpa.repository.HistoryRepository
import org.springframework.stereotype.Component

@Component
class HistoryDataSupport(private val historyRepository: HistoryRepository) : AbstractDataSupport() {

    fun randomHistory(entityFunction: (History) -> History = { it }): History {
        return historyRepository
                .save(entityFunction(randomGenerator.nextObject(History::class.java)))
    }
}
