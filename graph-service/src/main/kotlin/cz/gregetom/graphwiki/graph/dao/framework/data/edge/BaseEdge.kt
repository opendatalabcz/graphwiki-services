package cz.gregetom.graphwiki.graph.dao.framework.data.edge

/**
 * Parent for edges
 */
interface BaseEdge {
    val id: String
    // source vertex ID
    val source: String
    // target vertex ID
    val target: String
}

enum class EdgeType {
    RELATIONSHIP,
    NESTED_VERTEX
}
