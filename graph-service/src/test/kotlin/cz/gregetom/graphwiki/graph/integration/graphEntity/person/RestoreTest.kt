package cz.gregetom.graphwiki.graph.integration.graphEntity.person

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.PersonTO
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class RestoreTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport

    @Test
    fun restorePersonTest() {
        val inactivePerson = personDataSupport.randomInactivePerson()
        httpPut.doPutEmptyBody(PersonApiLinks.restore(inactivePerson.id).toUri())
        val restoredPerson = httpGet.doGet(PersonTO::class, PersonApiLinks.self(inactivePerson.id).toUri())
        assertThat(restoredPerson.state).isEqualTo(GraphEntityState.ACTIVE)

        // history record should be created
        this.historyStateTransitionRecordWasCreated(restoredPerson.id, inactivePerson.state, GraphEntityState.ACTIVE)
    }
}
