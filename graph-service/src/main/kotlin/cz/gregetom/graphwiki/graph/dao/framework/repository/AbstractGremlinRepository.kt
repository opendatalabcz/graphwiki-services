package cz.gregetom.graphwiki.graph.dao.framework.repository

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Element
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractGremlinRepository {

    protected val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    @Autowired
    protected lateinit var datasource: GraphTraversalSource

    /**
     * Add property step to graph traversal for every entity attribute.
     */
    protected fun <T : Element> GraphTraversal<T, T>.setPropertiesFromEntity(
            entity: Any,
            exclude: List<String> = emptyList()): GraphTraversal<T, T> {

        objectMapper.convertValue(entity, MutableMap::class.java)
                .filterNot { exclude.contains(it.key) }
                .filter { it.value != null }
                .filter { it.key !== org.apache.tinkerpop.gremlin.structure.T.id }
                .filter { it.key != "id" }
                // VertexProperty.Cardinality.single works by default
                .forEach { this.property(it.key, it.value) }
        return this
    }
}
