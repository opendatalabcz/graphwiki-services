package cz.gregetom.graphwiki.comment.dao.data

import cz.gregetom.graphwiki.commons.web.util.IpAddressProvider
import java.net.InetAddress
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "COMMENT")
data class Comment(
        @Id
        val id: String,
        val entityId: String,
        val author: String,
        val created: OffsetDateTime,
        val root: Boolean,
        val text: String,
        @OrderBy("created")
        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "parent_id")
        val replies: MutableSet<Comment> = mutableSetOf(),
        val ipAddress: InetAddress = IpAddressProvider.provideClientIpAddress()
)
