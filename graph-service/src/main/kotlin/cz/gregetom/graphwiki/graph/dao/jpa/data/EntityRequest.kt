package cz.gregetom.graphwiki.graph.dao.jpa.data

import cz.gregetom.graphwiki.api.graph.model.EntityRequestState
import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.commons.web.util.IpAddressProvider
import java.net.InetAddress
import java.net.URI
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "ENTITY_REQUEST")
data class EntityRequest(
        @Id
        override val id: String,
        override val created: OffsetDateTime,
        override val author: String,
        override var assignee: String?,
        @Enumerated(EnumType.STRING)
        override val entityType: GraphEntityType,
        override val entityId: String,
        @Enumerated(EnumType.STRING)
        override var state: EntityRequestState,
        override var taskUrl: URI? = null,
        val ipAddress: InetAddress = IpAddressProvider.provideClientIpAddress()
) : RelatedEntity<EntityRequestState>
