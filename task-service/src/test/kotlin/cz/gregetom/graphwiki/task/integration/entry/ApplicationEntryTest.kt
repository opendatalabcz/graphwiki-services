package cz.gregetom.graphwiki.task.integration.entry

import cz.gregetom.graphwiki.api.java.task.api.ApplicationEntryApi
import cz.gregetom.graphwiki.api.task.model.ApplicationEntryActions
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
        assertThat(anonymousUserEntryActions.taskInboxTeam).isNull()
        assertThat(anonymousUserEntryActions.taskInboxTeamWithAssigned).isNull()
        assertThat(anonymousUserEntryActions.taskInboxPrivate).isNull()
        assertThat(anonymousUserEntryActions.openTaskCount).isNull()

        val loggedUserEntryActions = httpGet.doGet(ApplicationEntryActions::class,
                linkTo(methodOn(ApplicationEntryApi::class.java).getEntryActions()).toUri(), TestUsers.USER)
        assertThat(loggedUserEntryActions.taskInboxTeam).isNull()
        assertThat(loggedUserEntryActions.taskInboxTeamWithAssigned).isNull()
        assertThat(loggedUserEntryActions.taskInboxPrivate).isNull()
        assertThat(loggedUserEntryActions.openTaskCount).isNull()

        val adminUserEntryActions = httpGet.doGet(ApplicationEntryActions::class,
                linkTo(methodOn(ApplicationEntryApi::class.java).getEntryActions()).toUri(), TestUsers.ADMIN)
        assertThat(adminUserEntryActions.taskInboxTeam).isNotNull
        assertThat(adminUserEntryActions.taskInboxTeamWithAssigned).isNotNull
        assertThat(adminUserEntryActions.taskInboxPrivate).isNotNull
        assertThat(adminUserEntryActions.openTaskCount).isNotNull
    }
}
