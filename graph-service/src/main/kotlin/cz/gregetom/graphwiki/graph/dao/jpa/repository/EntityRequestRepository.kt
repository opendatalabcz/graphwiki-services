package cz.gregetom.graphwiki.graph.dao.jpa.repository

import cz.gregetom.graphwiki.api.graph.model.EntityRequestState
import cz.gregetom.graphwiki.graph.dao.jpa.data.EntityRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EntityRequestRepository : JpaRepository<EntityRequest, String>, RelatedEntityRepository<EntityRequest, EntityRequestState> {

    override fun findAllByEntityIdAndStateIsIn(entityId: String, vararg states: EntityRequestState): List<EntityRequest>
}
