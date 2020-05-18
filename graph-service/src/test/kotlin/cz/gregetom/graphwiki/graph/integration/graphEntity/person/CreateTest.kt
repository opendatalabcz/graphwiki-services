package cz.gregetom.graphwiki.graph.integration.graphEntity.person

import cz.gregetom.graphwiki.api.graph.model.EntityRequestTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.HistoryType
import cz.gregetom.graphwiki.api.graph.model.PersonTO
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class CreateTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport

    @Test
    fun createPersonTest() {
        // create
        val createPersonTO = personDataSupport.randomCreatePersonTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(PersonApiLinks.create().toUri(), createPersonTO)

        // get
        val entityRequestTO = httpGet.doGet(EntityRequestTO::class, location)
        val personTO = httpGet.doGet(PersonTO::class, entityRequestTO.links.entity.href)

        // check
        assertThat(createPersonTO).isEqualToComparingOnlyGivenFields(personTO,
                "givenName", "familyName", "gender", "nationality", "dateOfBirth", "occupation")
        assertThat(personTO.id).isNotNull()
        assertThat(personTO.created).isEqualTo(personTO.created)
        assertThat(personTO.author).isEqualTo(personTO.author)
        assertThat(personTO.state).isEqualTo(GraphEntityState.CONCEPT)
        assertThat(personTO.informationSource).isNotNull()
        assertThat(personTO.informationSourceHost).isNotNull()
        assertThat(personTO.state).isEqualTo(GraphEntityState.CONCEPT)
        assertThat(personTO.links.update).isNotNull
        assertThat(personTO.links.delete).isNotNull
        assertThat(personTO.links.restore).isNull()
        assertThat(personTO.links.complaintCreate).isNull()

        // history record should be created
        this.historyRelatedEntityRecordWasCreated(personTO.id, entityRequestTO.id, HistoryType.ENTITY_REQUEST_CREATED)
    }

    @Test
    fun createPersonByUserTest() {
        val createPersonTO = personDataSupport.randomCreatePersonTO()
        this.expectTaskCreating()
        httpPost.doPostAndExpect(PersonApiLinks.create().toUri(), createPersonTO, HttpStatus.CREATED, TestUsers.USER)
    }
}
