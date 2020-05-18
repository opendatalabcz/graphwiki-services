package cz.gregetom.graphwiki.graph.dao.gremlin.data.edge

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.RelationshipType
import cz.gregetom.graphwiki.graph.dao.framework.data.edge.BaseEdge
import cz.gregetom.graphwiki.graph.dao.framework.data.edge.EdgeType
import cz.gregetom.graphwiki.graph.dao.framework.data.edge.annotation.Edge
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphEntity
import java.net.URI
import java.time.OffsetDateTime

@Edge(type = EdgeType.RELATIONSHIP)
data class Relationship(
        override val id: String = "SHOULD_BE_FILLED_BY_JANUSGRAPH",
        override val author: String,
        override val created: OffsetDateTime,
        override var state: GraphEntityState,
        override val informationSource: URI,
        override val source: String,
        override val target: String,
        val type: RelationshipType,
        val description: String? = null
) : BaseEdge, GraphEntity() {

    override fun label(): String {
        return "$type relationship"
    }
}
