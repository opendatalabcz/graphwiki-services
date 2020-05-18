package cz.gregetom.graphwiki.graph.services.graphEntity

import cz.gregetom.graphwiki.api.graph.model.CreateRelationshipTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.graph.dao.gremlin.data.edge.Relationship
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.edge.RelationshipRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime

@Service
class RelationshipService(private val relationshipRepository: RelationshipRepository)
    : AbstractGraphEntityService<Relationship>(GraphEntityType.RELATIONSHIP, relationshipRepository) {

    /**
     * Find incoming edges for vertex.
     *
     * @param vertexId vertex id
     * @return list of incoming edges
     */
    fun findIncomingForVertex(vertexId: String): List<Relationship> {
        return relationshipRepository.findIncomingForVertex(vertexId)
    }

    /**
     * Find outgoing edges for vertex.
     *
     * @param vertexId vertex id
     * @return list of outgoing edges
     */
    fun findOutgoingForVertex(vertexId: String): List<Relationship> {
        return relationshipRepository.findOutgoingForVertex(vertexId)
    }

    /**
     * Create new relationship and entity request.
     *
     * @param createRelationshipTO new relationship value
     * @return id of related entity request
     */
    fun create(createRelationshipTO: CreateRelationshipTO): String {
        LOGGER.info("Create new relationship $createRelationshipTO")
        if (createRelationshipTO.source == createRelationshipTO.target) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Relationship source and target cannot be the same.")
        }
        return relationshipRepository.save(
                Relationship(
                        author = UserAccessor.currentUserIdOrThrow,
                        created = OffsetDateTime.now(),
                        source = createRelationshipTO.source,
                        target = createRelationshipTO.target,
                        type = createRelationshipTO.type,
                        state = GraphEntityState.CONCEPT,
                        informationSource = createRelationshipTO.informationSource,
                        description = createRelationshipTO.description
                )
        ).let { createRelatedEntityRequest(it) }
    }

    /**
     * Update relationship
     * @param id id of relationship to be updated
     * @param createRelationshipTO new relationship value
     */
    fun update(id: String, createRelationshipTO: CreateRelationshipTO) {
        LOGGER.info("Update relationship $id with $createRelationshipTO")
        relationshipRepository.update(
                relationshipRepository.findById(id).copy(
                        type = createRelationshipTO.type,
                        informationSource = createRelationshipTO.informationSource,
                        description = createRelationshipTO.description
                )
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RelationshipService::class.java)
    }
}
