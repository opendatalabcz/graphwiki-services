package cz.gregetom.graphwiki.graph.dao.jpa.repository

import cz.gregetom.graphwiki.graph.dao.jpa.data.RelatedEntity

interface RelatedEntityRepository<T : RelatedEntity<U>, U> {

    fun getOne(id: String): T

    fun save(apply: T): T

    fun findAllByEntityIdAndStateIsIn(entityId: String, vararg states: U): List<T>
}
