package cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation

import cz.gregetom.graphwiki.graph.dao.framework.fulltextSearch.FulltextSearchProvider

/**
 * This annotation is used for marking property to be used for fulltext search.
 *
 * @see FulltextSearchProvider
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class FulltextSearchProperty
