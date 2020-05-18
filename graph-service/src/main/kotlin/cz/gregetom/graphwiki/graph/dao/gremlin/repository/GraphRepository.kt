package cz.gregetom.graphwiki.graph.dao.gremlin.repository

import cz.gregetom.graphwiki.graph.dao.framework.data.edge.EdgeType
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType
import cz.gregetom.graphwiki.graph.dao.framework.repository.vertex.VertexRepositoryProvider
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphData
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.edge.RelationshipRepository
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.`__`.*
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.springframework.stereotype.Component

@Component
class GraphRepository(private val datasource: GraphTraversalSource,
                      private val relationshipRepository: RelationshipRepository,
                      private val vertexRepositoryProvider: VertexRepositoryProvider
) {

    fun findVertexById(id: String): BaseVertex {
        return datasource
                .findValidVertexById(id)
                .nextOrThrow(BaseVertex::class, id)
                .let { vertexRepositoryProvider.getRepositoryForType(VertexType.valueOf(it.label())).findById(it.id().toString()) }
    }

    fun getGraphData(id: String, depth: Int): GraphData {
        val graph = datasource.findValidVertexById(id)
                .repeat(
                        bothE().hasLabel(EdgeType.RELATIONSHIP.name)
                                .onlyValidElement()
                                .where(inV().onlyValidElement())
                                .where(outV().onlyValidElement())
                                .subgraph("subgraph")
                                .bothV().simplePath()
                ).times(depth)
                .cap<TinkerGraph>("subgraph")
                .nextOrThrow(BaseVertex::class, id)
        return GraphData(
                rootVertex = findVertexById(id),
                vertices = graph.vertices().iterator().asSequence().map { findVertexById(it.id().toString()) }.toSet(),
                edges = graph.edges().asSequence().map { relationshipRepository.findById(it.id().toString()) }.toSet()
        )
    }

    fun getTinkerGraphForExport(id: String, depth: Int): TinkerGraph {
        return datasource.findValidVertexById(id)
                .repeat(
                        bothE().or(
                                identity<Edge>()
                                        .onlyValidElement()
                                        .where(inV().onlyValidElement())
                                        .where(outV().onlyValidElement()),
                                identity<Edge>().hasLabel(EdgeType.NESTED_VERTEX.name)
                        )
                                .subgraph("subgraph")
                                .bothV().simplePath()
                ).times(depth)
                .cap<TinkerGraph>("subgraph")
                .nextOrThrow(BaseVertex::class, id)
    }
}
