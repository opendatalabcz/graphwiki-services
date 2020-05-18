package cz.gregetom.graphwiki.graph.dao.jpa.repository

import cz.gregetom.graphwiki.api.graph.model.ComplaintState
import cz.gregetom.graphwiki.graph.dao.jpa.data.Complaint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ComplaintRepository : JpaRepository<Complaint, String>, RelatedEntityRepository<Complaint, ComplaintState> {

    override fun findAllByEntityIdAndStateIsIn(entityId: String, vararg states: ComplaintState): List<Complaint>
}
