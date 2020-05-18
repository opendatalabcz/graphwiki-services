package cz.gregetom.graphwiki.graph.dao.jpa.data

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.HistoryType
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "HISTORY")
data class History(
        @Id
        val id: String,
        val created: OffsetDateTime,
        val author: String,
        val entityId: String,
        @Enumerated(EnumType.STRING)
        val type: HistoryType,
        @Enumerated(EnumType.STRING)
        val previousState: GraphEntityState? = null,
        @Enumerated(EnumType.STRING)
        val currentState: GraphEntityState? = null,
        val relatedEntityId: String? = null
)
