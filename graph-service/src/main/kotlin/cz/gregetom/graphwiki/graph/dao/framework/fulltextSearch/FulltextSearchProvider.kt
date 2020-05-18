package cz.gregetom.graphwiki.graph.dao.framework.fulltextSearch

import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation.FulltextSearchProperty
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.findAllValidVertices
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.`__`.has
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.attribute.Text
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

/**
 * Provide support for fulltext search.
 * Only properties annotated with [FulltextSearchProperty] will be included in search query.
 *
 * @param kClass vertex KClass
 * @param vertexType vertex type
 * @param datasource connection to JanusGraph database
 */
class FulltextSearchProvider(private val kClass: KClass<out BaseVertex>,
                             private val vertexType: VertexType,
                             private val datasource: GraphTraversalSource) {

    private val fulltextSearchProperties = kClass.declaredMemberProperties
            .filter { it.findAnnotation<FulltextSearchProperty>() !== null }
            .toSet()


    /**
     * Search all vertices matching search query.
     *
     * @param query search query
     * @param pageable include only specific page
     * @return list of vertices matching search query
     */
    fun fulltextSearch(query: String, pageable: Pageable): List<Vertex> {
        return if (fulltextSearchProperties.isNotEmpty()) {
            fulltextSearchTraversal(query).range(0, pageable.offset).toList()
        } else {
            LOGGER.warn("No ${FulltextSearchProperty::class} provided in $kClass, search will be skipped")
            emptyList()
        }
    }

    /**
     * Get count of all vertices matching search query.
     *
     * @param query search query
     * @return count of matching vertices
     */
    fun fulltextSearchCount(query: String): Long {
        return if (fulltextSearchProperties.isNotEmpty()) {
            return fulltextSearchTraversal(query).count().next()
        } else {
            LOGGER.warn("No ${FulltextSearchProperty::class} provided in $kClass, search will be skipped")
            0
        }
    }

    private fun fulltextSearchTraversal(query: String): GraphTraversal<Vertex, Vertex> {
        return datasource.findAllValidVertices()
                .hasLabel(vertexType.name)
                .or(*fulltextSearchProperties.flatMap { property ->
                    query.split(" ").filter { it.isNotBlank() }.map { has<String>(property.name, Text.textContainsFuzzy(it)) }
                }.toTypedArray())
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FulltextSearchProvider::class.java)
    }
}
