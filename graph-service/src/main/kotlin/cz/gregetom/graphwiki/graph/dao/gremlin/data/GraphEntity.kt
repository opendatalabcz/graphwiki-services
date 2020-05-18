package cz.gregetom.graphwiki.graph.dao.gremlin.data

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import java.net.URI
import java.time.OffsetDateTime

abstract class GraphEntity {
    abstract val id: String
    abstract val author: String
    abstract val created: OffsetDateTime
    abstract var state: GraphEntityState
    abstract val informationSource: URI

    abstract fun label(): String
}
