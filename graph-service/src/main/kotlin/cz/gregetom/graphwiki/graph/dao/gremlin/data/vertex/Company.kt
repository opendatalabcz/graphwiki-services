package cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexVisitor
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation.FulltextSearchProperty
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation.NestedVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation.Vertex
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphEntity
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime

@Vertex(type = VertexType.COMPANY)
data class Company(
        override val id: String = "SHOULD_BE_FILLED_BY_JANUSGRAPH",
        override val author: String,
        override val created: OffsetDateTime,
        override var state: GraphEntityState,
        override val informationSource: URI,
        @FulltextSearchProperty
        val officialName: String,
        val registrationNumber: String,
        @NestedVertex(name = "headquarters")
        val headquarters: Address,
        val industry: String,
        val inception: LocalDate?
) : BaseVertex, GraphEntity() {

    override fun label(): String {
        return officialName
    }

    override fun <T> accept(visitor: VertexVisitor<T>): T {
        return visitor.visit(this)
    }
}

