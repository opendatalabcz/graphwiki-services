package cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation

import cz.gregetom.graphwiki.graph.dao.framework.fulltextSearch.FulltextSearchProvider
import cz.gregetom.graphwiki.graph.dao.framework.fulltextSearch.VertexFulltextSearchProxy

/**
 * This annotation is used for marking property to be used for fulltext search.
 *
 * @see FulltextSearchProvider
 * @see VertexFulltextSearchProxy
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class FulltextSearchProperty
