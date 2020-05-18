package cz.gregetom.graphwiki.graph.web.graph

import cz.gregetom.graphwiki.api.graph.api.GraphApi
import cz.gregetom.graphwiki.api.graph.model.GraphTO
import cz.gregetom.graphwiki.api.graph.model.VertexTO
import cz.gregetom.graphwiki.graph.services.graph.GraphService
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping
@Validated
class GraphController(private val graphService: GraphService) : GraphApi {

    @Transactional(readOnly = true)
    override fun getGraph(@NotNull @Size(max = 50) @RequestParam vertexId: String): ResponseEntity<GraphTO> {
        return ResponseEntity.ok(graphService.getGraph(vertexId))
    }

    @Transactional(readOnly = true)
    override fun findVertexById(@Size(max = 50) @PathVariable vertexId: String): ResponseEntity<VertexTO> {
        return ResponseEntity.ok(graphService.findVertexById(vertexId))
    }
}
