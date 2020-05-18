package cz.gregetom.graphwiki.graph.web.search

import cz.gregetom.graphwiki.api.graph.api.SearchApi
import cz.gregetom.graphwiki.api.graph.model.SearchResult
import cz.gregetom.graphwiki.graph.services.search.SearchService
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping
@Validated
class SearchController(private val searchService: SearchService) : SearchApi {

    @Transactional(readOnly = true)
    override fun fulltextSearch(@NotNull @Size(min = 3, max = 50) @RequestParam query: String,
                                @NotNull @Min(1) @RequestParam page: Int): ResponseEntity<SearchResult> {
        return ResponseEntity.ok(searchService.search(query, page))
    }
}
