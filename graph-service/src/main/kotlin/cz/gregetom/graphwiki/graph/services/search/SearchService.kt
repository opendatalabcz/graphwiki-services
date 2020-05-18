package cz.gregetom.graphwiki.graph.services.search

import cz.gregetom.graphwiki.api.graph.model.SearchResult
import cz.gregetom.graphwiki.api.graph.model.SearchResultLinks
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex.CompanyRepository
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex.PersonRepository
import cz.gregetom.graphwiki.graph.web.SearchApiLinks
import cz.gregetom.graphwiki.graph.web.search.SearchRecordMapper
import cz.gregetom.graphwiki.graph.web.toLinkTO
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class SearchService(
        private val personRepository: PersonRepository,
        private val companyRepository: CompanyRepository,
        private val searchRecordMapper: SearchRecordMapper
) {

    /**
     * Search persons/companies matching search query (paging is used).
     *
     * @param query search query
     * @param page page number
     * @return matching records with count of all available records
     */
    fun search(query: String, page: Int): SearchResult {
        val personsCount = personRepository.fulltextSearchCount(query)
        val persons = personRepository.fulltextSearch(query, PageRequest.of(page, PAGE_SIZE))
                .map { searchRecordMapper.map(it) }
                .sortedBy { it.familyName }

        val companiesCount = companyRepository.fulltextSearchCount(query)
        val companies = companyRepository.fulltextSearch(query, PageRequest.of(page, PAGE_SIZE))
                .map { searchRecordMapper.map(it) }
                .sortedBy { it.officialName }

        return SearchResult(
                anyDataAvailable = persons.size + companies.size > 0,
                personsAvailableCount = personsCount,
                persons = persons,
                companiesAvailableCount = companiesCount,
                companies = companies,
                links = SearchResultLinks(
                        nextPage = if (persons.size < personsCount || companies.size < companiesCount) {
                            SearchApiLinks.fulltextSearch(query, page + 1).toLinkTO()
                        } else null
                )
        )
    }

    companion object {
        internal const val PAGE_SIZE = 10
    }
}
