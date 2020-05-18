package cz.gregetom.graphwiki.comment.web

import cz.gregetom.graphwiki.api.comment.model.LinkTO
import cz.gregetom.graphwiki.api.java.comment.api.CommentApi
import cz.gregetom.graphwiki.api.java.user.api.UserApi
import cz.gregetom.graphwiki.comment.web.controller.CommentController
import cz.gregetom.graphwiki.commons.web.LinkBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component
import java.net.URI

object CommentApiLinks {
    fun list(entityId: String): Link {
        return linkTo(methodOn(CommentApi::class.java).findAllByEntityId(entityId)).withRel("list")
    }

    fun create(entityId: String): Link {
        return linkTo(methodOn(CommentApi::class.java).create(entityId, null)).withRel("create")
    }

    fun reply(id: String): Link {
        return linkTo(methodOn(CommentApi::class.java).reply(id, null)).withRel("reply")
    }

    fun update(id: String): Link {
        return linkTo(methodOn(CommentApi::class.java).update(id, null)).withRel("update")
    }

    fun delete(id: String): Link {
        return linkTo(methodOn(CommentController::class.java).delete(id)).withRel("delete")
    }
}

@Component
class LinkFactory(
        @Value("\${graphwiki.web.services.user.base-url}")
        private val userServiceBaseUrl: URI
) {

    fun userById(userId: String, rel: String): Link {
        return LinkBuilder.anotherServiceLink(userServiceBaseUrl, methodOn(UserApi::class.java).findById(userId), rel)
    }
}

fun Link.toLinkTO(): LinkTO {
    return LinkTO(
            href = this.href,
            rel = this.rel.value()
    )
}
