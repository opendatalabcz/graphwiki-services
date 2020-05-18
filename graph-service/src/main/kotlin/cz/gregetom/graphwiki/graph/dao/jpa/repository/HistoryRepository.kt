package cz.gregetom.graphwiki.graph.dao.jpa.repository

import cz.gregetom.graphwiki.graph.dao.jpa.data.History
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HistoryRepository : JpaRepository<History, String> {

    fun findAllByEntityId(entityId: String): List<History>
}
