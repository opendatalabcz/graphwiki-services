package cz.gregetom.graphwiki.graph.web.graphEntity.relationship

import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.api.graph.model.RelationshipTO
import cz.gregetom.graphwiki.api.graph.model.RelationshipTOLinks
import cz.gregetom.graphwiki.graph.dao.gremlin.data.edge.Relationship
import cz.gregetom.graphwiki.graph.services.graph.GraphService
import cz.gregetom.graphwiki.graph.web.*
import cz.gregetom.graphwiki.graph.web.graphEntity.AbstractGraphEntityMapper
import org.springframework.stereotype.Component

@Component
class RelationshipMapper(
        private val graphService: GraphService,
        private val linkFactory: LinkFactory
) : AbstractGraphEntityMapper() {

    fun map(relationship: Relationship): RelationshipTO {
        return RelationshipTO(
                id = relationship.id,
                author = linkFactory.userSelf(relationship.author, "author").toLinkTO(),
                created = relationship.created,
                source = graphService.findVertexById(relationship.source),
                target = graphService.findVertexById(relationship.target),
                state = relationship.state,
                type = relationship.type,
                informationSource = relationship.informationSource,
                informationSourceHost = relationship.informationSource.host,
                description = relationship.description,
                links = mapLinks(relationship)
        )
    }

    private fun mapLinks(relationship: Relationship): RelationshipTOLinks {
        return RelationshipTOLinks(
                self = RelationshipApiLinks.self(relationship.id).toLinkTO(),
                update = relationship.ifUpdateAvailable { RelationshipApiLinks.update(relationship.id).toLinkTO() },
                delete = relationship.ifDeleteAvailable { RelationshipApiLinks.delete(relationship.id).toLinkTO() },
                restore = relationship.ifRestoreAvailable { RelationshipApiLinks.restore(relationship.id).toLinkTO() },
                complaintCreate = relationship.ifComplaintAvailable { ComplaintApiLinks.setup(relationship.id, GraphEntityType.RELATIONSHIP).toLinkTO() },
                complaintList = ComplaintApiLinks.entityRelated(relationship.id).toLinkTO(),
                history = HistoryApiLinks.forEntity(relationship.id).toLinkTO()
        )
    }
}
