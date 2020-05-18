package cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation

/**
 * This annotation is used for marking class an an nested vertex.
 *
 * @param name label of edge between nested and parent vertex
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class NestedVertex(val name: String)
