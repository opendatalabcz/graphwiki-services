package cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation

import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType

/**
 * This annotation is used for marking class an an vertex.
 *
 * @param type vertex type
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Vertex(val type: VertexType)
