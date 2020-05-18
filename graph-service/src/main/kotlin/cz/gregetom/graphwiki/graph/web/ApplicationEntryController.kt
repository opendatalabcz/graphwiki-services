package cz.gregetom.graphwiki.graph.web

import cz.gregetom.graphwiki.api.graph.api.ApplicationEntryApi
import cz.gregetom.graphwiki.api.graph.model.ApplicationEntryActions
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
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
                        personCreate = if (UserAccessor.hasRoleAny(Roles.ROLE_USER, Roles.ROLE_ADMIN)) PersonApiLinks.create().toLinkTO() else null,
                        companyCreate = if (UserAccessor.hasRoleAny(Roles.ROLE_USER, Roles.ROLE_ADMIN)) CompanyApiLinks.create().toLinkTO() else null,
                        relationshipCreate = if (UserAccessor.hasRoleAny(Roles.ROLE_USER, Roles.ROLE_ADMIN)) RelationshipApiLinks.create().toLinkTO() else null,
                        search = SearchApiLinks.fulltextSearchWithoutQueryParams().toLinkTO()
                )
        )
    }
}
