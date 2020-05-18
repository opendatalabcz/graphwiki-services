package cz.gregetom.graphwiki.user.web.controller

import cz.gregetom.graphwiki.api.user.api.ApplicationEntryApi
import cz.gregetom.graphwiki.api.user.model.ApplicationEntryActions
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.user.web.AuthenticationApiLinks
import cz.gregetom.graphwiki.user.web.UserApiLinks
import cz.gregetom.graphwiki.user.web.toLinkTO
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
@Validated
class ApplicationEntryController : ApplicationEntryApi {

    override fun getEntryActions(): ResponseEntity<ApplicationEntryActions> {
        return ResponseEntity.ok(
                ApplicationEntryActions(
                        authInfo = AuthenticationApiLinks.authInfo().toLinkTO(),
                        authenticate = AuthenticationApiLinks.authenticate().toLinkTO(),
                        register = UserApiLinks.register().toLinkTO(),
                        loggedUser = if (UserAccessor.isLogged) {
                            UserApiLinks.logged().toLinkTO()
                        } else null,
                        userValidation = UserApiLinks.validation().toLinkTO()
                )
        )
    }
}
