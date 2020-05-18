package cz.gregetom.graphwiki.graph.services.graph

import cz.gregetom.graphwiki.api.graph.model.*
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphData
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.GraphRepository
import cz.gregetom.graphwiki.graph.web.ExportApiLinks
import cz.gregetom.graphwiki.graph.web.RelationshipApiLinks
import cz.gregetom.graphwiki.graph.web.graph.VertexMapperVisitor
import cz.gregetom.graphwiki.graph.web.toLinkTO
import org.springframework.stereotype.Service

@Service
class GraphService(private val graphRepository: GraphRepository) {

    /**
     * Get graph with specific root vertex.
     *
     * @param id id of graph root vertex
     * @return graph data
     */
    fun getGraph(vertexId: String): GraphTO {
        val graphData = graphRepository.getGraphData(vertexId, DEFAULT_GRAPH_DEPTH)
        val mapperVisitor = VertexMapperVisitor(graphData.rootVertex)

        // if subgraph is empty, display only root vertex
        val verticesToDisplay = if (graphData.vertices.isEmpty()) {
            graphData.vertices.plus(graphData.rootVertex)
        } else {
            graphData.vertices
        }

        return GraphTO(
                rootNode = graphData.rootVertex.accept(mapperVisitor),
                nodes = verticesToDisplay.map { it.accept(mapperVisitor) }.toList(),
                edges = graphData.edges.map {
                    EdgeTO(
                            id = it.id,
                            source = it.source,
                            target = it.target,
                            label = it.type.toString(),
                            links = EdgeTOLinks(
                                    self = RelationshipApiLinks.self(it.id).toLinkTO()
                            )
                    )
                },
                links = GraphTOLinks(
                        exportGraphML = ifExportAvailable(graphData) { ExportApiLinks.graphML(vertexId).toLinkTO() },
                        exportClueMaker = ifExportAvailable(graphData) { ExportApiLinks.clueMaker(vertexId).toLinkTO() }
                )
        )
    }

    fun findVertexById(vertexId: String): VertexTO {
        return graphRepository.findVertexById(vertexId).accept(defaultGraphEntityMapperVisitor)
    }

    companion object {
        private val defaultGraphEntityMapperVisitor = VertexMapperVisitor()
        internal const val DEFAULT_GRAPH_DEPTH = 10
    }
}

private fun ifExportAvailable(graphData: GraphData, block: () -> LinkTO?): LinkTO? {
    return if (graphData.vertices.isNotEmpty()) {
        block()
    } else {
        null
    }
}
