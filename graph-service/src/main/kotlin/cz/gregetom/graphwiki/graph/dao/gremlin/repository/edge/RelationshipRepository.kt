package cz.gregetom.graphwiki.graph.dao.gremlin.repository.edge

import cz.gregetom.graphwiki.graph.dao.framework.repository.edge.AbstractEdgeRepository
import cz.gregetom.graphwiki.graph.dao.gremlin.data.edge.Relationship
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.GraphEntityRepository
import org.springframework.stereotype.Component

@Component
class RelationshipRepository : AbstractEdgeRepository<Relationship>(Relationship::class), GraphEntityRepository<Relationship>
