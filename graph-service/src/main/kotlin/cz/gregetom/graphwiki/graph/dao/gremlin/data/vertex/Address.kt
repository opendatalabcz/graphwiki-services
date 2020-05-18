package cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex

import cz.gregetom.graphwiki.api.graph.model.Country
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexVisitor
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation.Vertex
import java.time.OffsetDateTime

@Vertex(type = VertexType.ADDRESS)
data class Address(
        override val id: String = "SHOULD_BE_FILLED_BY_JANUSGRAPH",
        override val author: String,
        override val created: OffsetDateTime,
        val street: String,
        val houseNumber: String,
        val postalCode: String,
        val landRegistryNumber: String?,
        val city: String,
        val country: Country
) : BaseVertex {

    override fun <T> accept(visitor: VertexVisitor<T>): T {
        return visitor.visit(this)
    }

    companion object {
        fun format(street: String, postalCode: String, landRegistryNumber: String?, houseNumber: String, city: String, country: Country): String {
            return "$street ${landRegistryNumber.valueOrEmpty("/")}$houseNumber, $postalCode $city, $country"
        }

        private fun String?.valueOrEmpty(suffix: String): String {
            return this?.let { it + suffix } ?: ""
        }
    }
}
