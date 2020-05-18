package cz.gregetom.graphwiki.graph.dao.framework.repository.edge

import cz.gregetom.graphwiki.graph.dao.framework.data.edge.BaseEdge
import cz.gregetom.graphwiki.graph.dao.framework.data.edge.EdgeType
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.repository.AbstractGremlinRepository
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.findValidEdgeById
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.findValidVertexById
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.nextOrThrow
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.onlyValidElement
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.`__`.inV
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.`__`.outV
import org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions
import org.apache.tinkerpop.gremlin.structure.Edge
import org.janusgraph.graphdb.relations.RelationIdentifier
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import cz.gregetom.graphwiki.graph.dao.framework.data.edge.annotation.Edge as EdgeAnnotation

abstract class AbstractEdgeRepository<T : BaseEdge>(private val kClass: KClass<T>) : AbstractGremlinRepository(), EdgeRepositoryApi<T> {

    private val edgeType: EdgeType = kClass.findAnnotation<EdgeAnnotation>()?.type
            ?: throw IllegalStateException("Edge of type $kClass must be annotated with ${EdgeAnnotation::class}")

    override fun save(entity: T): T {
        val entityId = datasource.addE(this.edgeType.name)
                .from(datasource.findValidVertexById(entity.source).nextOrThrow(BaseVertex::class, entity.source))
                .to(datasource.findValidVertexById(entity.target).nextOrThrow(BaseVertex::class, entity.target))
                .setPropertiesFromEntity(entity)
                .next().id().toString()
        return findByIdWithoutUserPolicy(entityId)
    }

    override fun findById(id: String): T {
        val dataMap = getEdgeOfCurrentTypeTraversalById(id).valueMap<Any>().with(WithOptions.tokens).nextOrThrow(kClass, id)
        return resolveEntityFromMap(dataMap)
    }

    override fun update(entity: T): T {
        val edgeId = getEdgeOfCurrentTypeTraversalById(entity.id)
                .setPropertiesFromEntity(entity)
                .nextOrThrow(kClass, entity.id)
                .id().toString()
        return findById(edgeId)
    }

    override fun findIncomingForVertex(vertexId: String): List<T> {
        return datasource.findValidVertexById(vertexId)
                .inE(EdgeType.RELATIONSHIP.name).onlyValidElement()
                .filter(outV().onlyValidElement())
                .toSet()
                .map { findById(it.id().toString()) }
    }

    override fun findOutgoingForVertex(vertexId: String): List<T> {
        return datasource.findValidVertexById(vertexId)
                .outE(EdgeType.RELATIONSHIP.name).onlyValidElement()
                .filter(inV().onlyValidElement())
                .toSet()
                .map { findById(it.id().toString()) }
    }

    private fun resolveEntityFromMap(dataMap: MutableMap<Any, Any>): T {
        dataMap[BaseEdge::source.name] = (dataMap[org.apache.tinkerpop.gremlin.structure.T.id] as RelationIdentifier).outVertexId
        dataMap[BaseEdge::target.name] = (dataMap[org.apache.tinkerpop.gremlin.structure.T.id] as RelationIdentifier).inVertexId
        dataMap.replace(org.apache.tinkerpop.gremlin.structure.T.id, (dataMap[org.apache.tinkerpop.gremlin.structure.T.id] as RelationIdentifier).toString())
        return objectMapper.convertValue(dataMap, kClass.java)
    }

    /**
     * Do not check, if current user is allowed to get entity.
     */
    private fun findByIdWithoutUserPolicy(id: String): T {
        val dataMap = datasource.E(id).valueMap<Any>().with(WithOptions.tokens).nextOrThrow(kClass, id)
        return resolveEntityFromMap(dataMap)
    }

    private fun getEdgeOfCurrentTypeTraversalById(id: String): GraphTraversal<Edge, Edge> {
        return datasource.findValidEdgeById(id).hasLabel(this.edgeType.name)
    }
}
