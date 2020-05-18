package cz.gregetom.graphwiki.user.web

import cz.gregetom.graphwiki.api.java.user.api.AuthenticationApi
import cz.gregetom.graphwiki.api.java.user.api.UserApi
import cz.gregetom.graphwiki.api.user.model.LinkTO
import cz.gregetom.graphwiki.user.web.controller.AuthenticationController
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

object UserApiLinks {
    fun self(id: String): Link {
        return linkTo(methodOn(UserApi::class.java).findById(id)).withSelfRel()
    }

    fun register(): Link {
        return linkTo(methodOn(UserApi::class.java).create(null)).withRel("register")
    }

    fun logged(): Link {
        return linkTo(methodOn(UserApi::class.java).loggedUser()).withRel("logged")
    }

    fun validation(): Link {
        return Link(
                linkTo(methodOn(UserApi::class.java).validation(null))
                        .toUriComponentsBuilder()
                        .replaceQuery(null)
                        .build()
                        .toUriString(),
                "validate"
        )
    }
}

object AuthenticationApiLinks {
    fun authenticate(): Link {
        return linkTo(methodOn(AuthenticationApi::class.java).authenticate(null)).withRel("authenticate")
    }

    fun authInfo(): Link {
        return linkTo(methodOn(AuthenticationController::class.java).authInfo()).withRel("authInfo")
    }
}

fun Link.toLinkTO(): LinkTO {
    return LinkTO(
            href = this.href,
            rel = this.rel.value()
    )
}
