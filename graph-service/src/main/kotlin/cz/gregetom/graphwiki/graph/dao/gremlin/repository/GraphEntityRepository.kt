package cz.gregetom.graphwiki.graph.dao.gremlin.repository

import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphEntity

interface GraphEntityRepository<T : GraphEntity> {

    fun findById(id: String): T

    fun update(entity: T): T
}
