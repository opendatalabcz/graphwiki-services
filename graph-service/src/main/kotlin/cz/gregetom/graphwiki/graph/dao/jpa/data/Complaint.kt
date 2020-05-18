package cz.gregetom.graphwiki.graph.dao.jpa.data


import cz.gregetom.graphwiki.api.graph.model.ComplaintState
import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.commons.web.util.IpAddressProvider
import java.net.InetAddress
import java.net.URI
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "COMPLAINT")
data class Complaint(
        @Id
        override val id: String,
        override val entityId: String,
        @Enumerated(EnumType.STRING)
        override val entityType: GraphEntityType,
        override val author: String,
        override var assignee: String? = null,
        @Enumerated(EnumType.STRING)
        override var state: ComplaintState,
        override val created: OffsetDateTime,
        override var taskUrl: URI? = null,
        val title: String,
        val explanation: String,
        val ipAddress: InetAddress = IpAddressProvider.provideClientIpAddress()
) : RelatedEntity<ComplaintState>
