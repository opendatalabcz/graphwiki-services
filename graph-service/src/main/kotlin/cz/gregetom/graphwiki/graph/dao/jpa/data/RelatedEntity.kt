package cz.gregetom.graphwiki.graph.dao.jpa.data

import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import java.net.URI
import java.time.OffsetDateTime

interface RelatedEntity<U> {
    val id: String
    val created: OffsetDateTime
    val author: String
    var assignee: String?
    val entityType: GraphEntityType
    val entityId: String
    var state: U
    var taskUrl: URI?
}
