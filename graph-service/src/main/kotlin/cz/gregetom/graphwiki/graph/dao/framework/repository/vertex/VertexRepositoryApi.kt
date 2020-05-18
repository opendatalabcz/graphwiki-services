package cz.gregetom.graphwiki.graph.dao.framework.repository.vertex

import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.springframework.web.server.ResponseStatusException

interface VertexRepositoryApi<T : BaseVertex> {

    /**
     * Find out, if vertex type is supported with repository or not.
     */
    fun support(type: VertexType): Boolean

    /**
     * Save vertex.
     *
     * @param entity vertex
     * @return saved vertex
     */
    fun save(entity: T): T

    /**
     * Get graph traversal with steps for vertex save.
     *
     * @param entity vertex
     * @param vertexType vertex type
     * @param stepLabel label for marking new vertex in graph traversal
     * @return graph traversal
     */
    fun saveAsTraversal(entity: T,
                        vertexType: VertexType,
                        stepLabel: String): GraphTraversal<Vertex, Vertex>

    /**
     * Find vertex by id.
     *
     * @param id vertex id
     * @throws ResponseStatusException if edge is not available
     * @return vertex with specific id
     */
    fun findById(id: String): T

    /**
     * Update vertex.
     *
     * @param entity vertex
     * @return updated vertex
     */
    fun update(entity: T): T

    /**
     * Add update vertex steps to existing traversal.
     *
     * @param entity vertex
     * @param traversal existing graph traversal
     * @return graph traversal with steps for vertex update
     */
    fun updateAsTraversal(entity: T, traversal: GraphTraversal<Vertex, Vertex>): GraphTraversal<Vertex, Vertex>
}
