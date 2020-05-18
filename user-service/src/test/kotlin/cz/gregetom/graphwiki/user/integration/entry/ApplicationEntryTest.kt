package cz.gregetom.graphwiki.user.integration.entry

import cz.gregetom.graphwiki.api.java.user.api.ApplicationEntryApi
import cz.gregetom.graphwiki.api.user.model.ApplicationEntryActions
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.user.AbstractIntegrationTest
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

class ApplicationEntryTest : AbstractIntegrationTest() {

    @Test
    fun entryActionsTest() {
        val anonymousUserEntryActions = httpGet.doGet(ApplicationEntryActions::class,
                linkTo(methodOn(ApplicationEntryApi::class.java).getEntryActions()).toUri(), null)
        assertThat(anonymousUserEntryActions.loggedUser).isNull()

        val loggedUserEntryActions = httpGet.doGet(ApplicationEntryActions::class,
                linkTo(methodOn(ApplicationEntryApi::class.java).getEntryActions()).toUri(), TestUsers.USER)
        assertThat(loggedUserEntryActions.loggedUser).isNotNull

        val adminUserEntryActions = httpGet.doGet(ApplicationEntryActions::class,
                linkTo(methodOn(ApplicationEntryApi::class.java).getEntryActions()).toUri(), TestUsers.ADMIN)
        assertThat(adminUserEntryActions.loggedUser).isNotNull
    }
}
