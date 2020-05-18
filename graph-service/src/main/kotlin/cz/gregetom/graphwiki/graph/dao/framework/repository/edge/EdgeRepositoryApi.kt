package cz.gregetom.graphwiki.graph.dao.framework.repository.edge

import cz.gregetom.graphwiki.graph.dao.framework.data.edge.BaseEdge
import org.springframework.web.server.ResponseStatusException

interface EdgeRepositoryApi<T : BaseEdge> {

    /**
     * Save edge.
     *
     * @param entity edge
     * @return saved edge
     */
    fun save(entity: T): T

    /**
     * Find edge by id.
     *
     * @param id edge id
     * @throws ResponseStatusException if edge is not available
     * @return edge with specific id
     */
    fun findById(id: String): T

    /**
     * Update edge.
     *
     * @param entity edge
     * @return updated edge
     */
    fun update(entity: T): T

    /**
     * Find incoming edges for vertex.
     *
     * @param vertexId vertex id
     * @return list of incoming edges
     */
    fun findIncomingForVertex(vertexId: String): List<T>

    /**
     * Find outgoing edges for vertex.
     *
     * @param vertexId vertex id
     * @return list of outgoing edges
     */
    fun findOutgoingForVertex(vertexId: String): List<T>
}
