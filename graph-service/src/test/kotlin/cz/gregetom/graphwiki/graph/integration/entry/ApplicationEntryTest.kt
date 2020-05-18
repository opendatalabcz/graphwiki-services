package cz.gregetom.graphwiki.graph.integration.entry

import cz.gregetom.graphwiki.api.graph.model.ApplicationEntryActions
import cz.gregetom.graphwiki.api.java.graph.api.ApplicationEntryApi
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

class ApplicationEntryTest : AbstractIntegrationTest() {

    @Test
    fun entryActionsTest() {
        val anonymousUserEntryActions = httpGet.doGet(ApplicationEntryActions::class,
                linkTo(methodOn(ApplicationEntryApi::class.java).getEntryActions()).toUri(), null)
        assertThat(anonymousUserEntryActions.personCreate).isNull()
        assertThat(anonymousUserEntryActions.companyCreate).isNull()
        assertThat(anonymousUserEntryActions.relationshipCreate).isNull()

        val loggedUserEntryActions = httpGet.doGet(ApplicationEntryActions::class,
                linkTo(methodOn(ApplicationEntryApi::class.java).getEntryActions()).toUri(), TestUsers.USER)
        assertThat(loggedUserEntryActions.personCreate).isNotNull
        assertThat(loggedUserEntryActions.companyCreate).isNotNull
        assertThat(loggedUserEntryActions.relationshipCreate).isNotNull

        val adminUserEntryActions = httpGet.doGet(ApplicationEntryActions::class,
                linkTo(methodOn(ApplicationEntryApi::class.java).getEntryActions()).toUri(), TestUsers.ADMIN)
        assertThat(adminUserEntryActions.personCreate).isNotNull
        assertThat(adminUserEntryActions.companyCreate).isNotNull
        assertThat(adminUserEntryActions.relationshipCreate).isNotNull
    }
}
