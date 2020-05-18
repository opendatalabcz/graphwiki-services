package cz.gregetom.graphwiki.graph.web.history

import cz.gregetom.graphwiki.api.graph.api.HistoryApi
import cz.gregetom.graphwiki.api.graph.model.HistoryTO
import cz.gregetom.graphwiki.graph.services.history.HistoryService
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping
@Validated
class HistoryController(
        private val historyMapper: HistoryMapper,
        private val historyService: HistoryService
) : HistoryApi {

    @Transactional(readOnly = true)
    override fun findAllByEntityId(@NotNull @Size(max = 50) @RequestParam entityId: String): ResponseEntity<List<HistoryTO>> {
        return ResponseEntity.ok(historyService.findAllByEntityId(entityId).map { historyMapper.map(it) })
    }
}
