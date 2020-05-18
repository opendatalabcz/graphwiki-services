package cz.gregetom.graphwiki.graph.dao.gremlin.repository.edge

import cz.gregetom.graphwiki.graph.dao.framework.repository.edge.AbstractEdgeRepository
import cz.gregetom.graphwiki.graph.dao.gremlin.data.edge.Relationship
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.GraphEntityRepository

class RelationshipRepository : AbstractEdgeRepository<Relationship>(Relationship::class), GraphEntityRepository<Relationship>
