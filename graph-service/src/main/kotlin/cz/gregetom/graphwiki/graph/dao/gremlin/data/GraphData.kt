package cz.gregetom.graphwiki.graph.dao.gremlin.data

import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.gremlin.data.edge.Relationship

data class GraphData(
        val rootVertex: BaseVertex,
        val vertices: Set<BaseVertex>,
        val edges: Set<Relationship>
)
