package cz.gregetom.graphwiki.graph.dao.framework.fulltextSearch

import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import org.springframework.data.domain.Pageable

interface VertexFulltextSearchApi<T : BaseVertex> {

    /**
     * Search all vertices matching search query.
     *
     * @param query search query
     * @param pageable include only specific page
     * @return list of vertices matching search query
     */
    fun fulltextSearch(query: String, pageable: Pageable): List<T>

    /**
     * Get count of all vertices matching search query.
     *
     * @param query search query
     * @return count of matching vertices
     */
    fun fulltextSearchCount(query: String): Long
}
