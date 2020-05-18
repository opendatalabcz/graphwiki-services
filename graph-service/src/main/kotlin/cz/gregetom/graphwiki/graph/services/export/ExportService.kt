package cz.gregetom.graphwiki.graph.services.export

import com.fasterxml.jackson.databind.ObjectMapper
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.GraphRepository
import cz.gregetom.graphwiki.graph.services.graph.GraphService.Companion.DEFAULT_GRAPH_DEPTH
import cz.gregetom.graphwiki.graph.web.export.cluemaker.data.ClueMakerExport
import cz.gregetom.graphwiki.graph.web.export.cluemaker.mapper.ClueMakerEdgeMapper
import cz.gregetom.graphwiki.graph.web.export.cluemaker.mapper.ClueMakerVertexMapper
import cz.gregetom.graphwiki.graph.web.graph.VertexMapperVisitor
import org.apache.tinkerpop.gremlin.structure.io.IoCore
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class ExportService(private val graphRepository: GraphRepository, private val objectMapper: ObjectMapper) {

    /**
     * Export graph to GraphML.
     *
     * @param id id of graph root vertex
     * @return pair of filename and byte data
     */
    fun exportGraphML(id: String): Pair<String, ByteArray> {
        val graph = graphRepository.getTinkerGraphForExport(id, DEFAULT_GRAPH_DEPTH)
        val outputStream = ByteArrayOutputStream()
        outputStream.use { os -> graph.io(IoCore.graphml()).writer().normalize(true).create().writeGraph(os, graph) }
        return Pair(getFilenameByVertex(id), outputStream.toByteArray())
    }

    /**
     * Export to ClueMaker archive.
     *
     * @param id id of graph root vertex
     * @return pair of filename and byte data
     */
    fun exportClueMaker(id: String): Pair<String, ByteArray> {
        val graph = graphRepository.getTinkerGraphForExport(id, DEFAULT_GRAPH_DEPTH)

        val clueMakerNodes = graph.vertices().asSequence().map { ClueMakerVertexMapper.map(it) }.toList()
        val clueMakerEdges = graph.edges().asSequence().map { ClueMakerEdgeMapper.map(it) }.toList()
        val export = ClueMakerExport(nodes = clueMakerNodes, edges = clueMakerEdges)

        return Pair(getFilenameByVertex(id), objectMapper.writeValueAsBytes(export))
    }

    private fun getFilenameByVertex(id: String): String {
        return graphRepository.findVertexById(id).accept(defaultGraphEntityMapperVisitor).label
    }

    companion object {
        private val defaultGraphEntityMapperVisitor = VertexMapperVisitor()
    }
}
