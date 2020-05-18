package cz.gregetom.graphwiki.graph.web.export

import cz.gregetom.graphwiki.api.graph.api.ExportApi
import cz.gregetom.graphwiki.graph.services.export.ExportService
import cz.gregetom.graphwiki.graph.web.export.cluemaker.ClueMakerArchiveUtil
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping
@Validated
class ExportController(private val exportService: ExportService) : ExportApi {

    override fun exportGraphML(@NotNull @Size(max = 50) @RequestParam vertexId: String): ResponseEntity<Resource> {
        val export = exportService.exportGraphML(vertexId)
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"GraphWiki_export_${export.first.replace(" ", "_")}\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_DISPOSITION)
                .body(ByteArrayResource(export.second))
    }

    override fun exportClueMaker(@NotNull @Size(max = 50) @RequestParam vertexId: String): ResponseEntity<Resource> {
        val export = exportService.exportClueMaker(vertexId)
        val archive = ClueMakerArchiveUtil.getArchiveWithDataContent(export.second)
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"GraphWiki_export_${export.first.replace(" ", "_")}.spr\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_DISPOSITION)
                .body(ByteArrayResource(archive))
    }
}
