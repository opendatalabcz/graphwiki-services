package cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex

import cz.gregetom.graphwiki.api.graph.model.Country
import cz.gregetom.graphwiki.api.graph.model.Gender
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexVisitor
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation.FulltextSearchProperty
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation.Vertex
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphEntity
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime

@Vertex(type = VertexType.PERSON)
data class Person(
        override val id: String = "SHOULD_BE_FILLED_BY_JANUSGRAPH",
        override val author: String,
        override val created: OffsetDateTime,
        override var state: GraphEntityState,
        override val informationSource: URI,
        @FulltextSearchProperty
        val givenName: String,
        @FulltextSearchProperty
        val familyName: String,
        val gender: Gender,
        val nationality: Country,
        val dateOfBirth: LocalDate?,
        val occupation: String?
) : BaseVertex, GraphEntity() {

    override fun label(): String {
        return "$givenName $familyName"
    }

    override fun <T> accept(visitor: VertexVisitor<T>): T {
        return visitor.visit(this)
    }
}
