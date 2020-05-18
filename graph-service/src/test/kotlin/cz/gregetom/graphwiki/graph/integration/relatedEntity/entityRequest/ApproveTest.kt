package cz.gregetom.graphwiki.graph.integration.relatedEntity.entityRequest

import cz.gregetom.graphwiki.api.graph.model.*
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.jpa.repository.EntityRequestRepository
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.EntityRequestApiLinks
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class ApproveTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var entityRequestRepository: EntityRequestRepository

    @Test
    fun approveEntityRequestTest() {
        // create
        val createPersonTO = personDataSupport.randomCreatePersonTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(PersonApiLinks.create().toUri(), createPersonTO)
        val savedEntityRequest = httpGet.doGet(EntityRequestTO::class, location)

        // assign
        entityRequestRepository.save(entityRequestRepository.getOne(savedEntityRequest.id).copy(assignee = TestUsers.ADMIN.username))
        val assignedEntityRequest = httpGet.doGet(EntityRequestTO::class, location)
        assertThat(assignedEntityRequest.links.approve).isNotNull
        assertThat(assignedEntityRequest.links.reject).isNotNull

        // approve and check
        this.expectTaskFinishing(assignedEntityRequest.links.task!!, TestUsers.ADMIN.username)
        httpPut.doPutEmptyBody(assignedEntityRequest.links.approve!!.href)
        val approvedEntityRequest = httpGet.doGet(EntityRequestTO::class, location)
        assertThat(approvedEntityRequest.state).isEqualTo(EntityRequestState.APPROVED)
        assertThat(approvedEntityRequest.links.approve).isNull()
        assertThat(approvedEntityRequest.links.reject).isNull()

        // check graph entity
        val graphEntity = httpGet.doGet(PersonTO::class, approvedEntityRequest.links.entity.href)
        assertThat(graphEntity.state).isEqualTo(GraphEntityState.ACTIVE)
        val graphEntityHistory = httpGet.doGet(Array<HistoryTO>::class, graphEntity.links.history.href)
        assertThat(graphEntityHistory.find { it.type === HistoryType.ENTITY_REQUEST_APPROVED }).isNotNull
    }

    @Test
    fun approveNotAssignedEntityRequestTest() {
        val createPersonTO = personDataSupport.randomCreatePersonTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(PersonApiLinks.create().toUri(), createPersonTO)
        val savedEntityRequest = httpGet.doGet(EntityRequestTO::class, location)
        httpPut.doPutAndExpect(EntityRequestApiLinks.reject(savedEntityRequest.id).toUri(), HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
