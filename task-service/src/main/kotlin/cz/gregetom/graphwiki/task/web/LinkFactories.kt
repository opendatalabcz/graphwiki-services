package cz.gregetom.graphwiki.task.web

import cz.gregetom.graphwiki.api.java.user.api.UserApi
import cz.gregetom.graphwiki.api.task.model.LinkTO
import cz.gregetom.graphwiki.commons.web.LinkBuilder
import cz.gregetom.graphwiki.task.web.controller.TaskController
import org.springframework.beans.factory.annotation.Value
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.stereotype.Component
import java.net.URI

object TaskApiLinks {
    fun taskAssign(taskId: String, userId: String?): Link {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController::class.java).assign(taskId, userId)).withRel("assign").expand()
    }

    fun openTaskCount(): Link {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController::class.java).openTaskCount()).withRel("openTaskCount")
    }

    fun teamInbox(showAssigned: Boolean): Link {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController::class.java).teamInbox(showAssigned)).withRel("teamInbox")
    }

    fun privateInbox(): Link {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController::class.java).privateInbox()).withRel("privateInbox")
    }

    fun self(id: String): Link {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController::class.java).findById(id)).withRel("self")
    }
}

@Component
class LinkFactory(
        @Value("\${graphwiki.web.services.user.base-url}")
        private val userServiceBaseUrl: URI
) {

    fun userById(userId: String, rel: String): Link {
        return LinkBuilder.anotherServiceLink(userServiceBaseUrl, WebMvcLinkBuilder.methodOn(UserApi::class.java).findById(userId), rel)
    }
}

fun Link.toLinkTO(): LinkTO {
    return LinkTO(
            href = this.href,
            rel = this.rel.value()
    )
}

fun URI.toLinkTO(rel: String): LinkTO {
    return LinkTO(
            href = this.toString(),
            rel = rel
    )
}
