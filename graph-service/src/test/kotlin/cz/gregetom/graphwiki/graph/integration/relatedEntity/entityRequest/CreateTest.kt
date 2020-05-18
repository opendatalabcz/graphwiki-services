package cz.gregetom.graphwiki.graph.integration.relatedEntity.entityRequest

import cz.gregetom.graphwiki.api.graph.model.EntityRequestState
import cz.gregetom.graphwiki.api.graph.model.EntityRequestTO
import cz.gregetom.graphwiki.api.graph.model.HistoryType
import cz.gregetom.graphwiki.api.graph.model.PersonTO
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.jpa.repository.EntityRequestRepository
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class CreateTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var entityRequestRepository: EntityRequestRepository

    @Test
    fun createEntityRequestTest() {
        // create
        val createPersonTO = personDataSupport.randomCreatePersonTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(PersonApiLinks.create().toUri(), createPersonTO)

        // get and check
        val entityRequest = httpGet.doGet(EntityRequestTO::class, location)
        assertThat(entityRequest.id).isNotNull()
        assertThat(entityRequest.author).isNotNull
        assertThat(entityRequest.created).isNotNull()
        assertThat(entityRequest.state).isEqualTo(EntityRequestState.NEW)
        assertThat(entityRequest.assignee).isNull()
        assertThat(entityRequest.links.commentCreate).isNotNull
        assertThat(entityRequest.links.approve).isNull()
        assertThat(entityRequest.links.reject).isNull()
        assertThat(entityRequest.links.task).isNotNull

        // get and check related graph entity
        val person = httpGet.doGet(PersonTO::class, entityRequest.links.entity.href)
        assertThat(createPersonTO).isEqualToComparingOnlyGivenFields(person,
                "givenName", "familyName", "gender", "nationality", "dateOfBirth", "occupation")

        this.historyRelatedEntityRecordWasCreated(person.id, entityRequest.id, HistoryType.ENTITY_REQUEST_CREATED)

        // ip address is not available on frontend
        assertThat(entityRequestRepository.findAll().first().ipAddress.canonicalHostName).isNotNull()
    }
}
