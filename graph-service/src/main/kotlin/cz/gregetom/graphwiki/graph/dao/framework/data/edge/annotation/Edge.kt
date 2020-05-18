package cz.gregetom.graphwiki.graph.dao.framework.data.edge.annotation

import cz.gregetom.graphwiki.graph.dao.framework.data.edge.EdgeType

/**
 * This annotation is used for marking class an an edge.
 *
 * @param type edge type
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Edge(val type: EdgeType)
