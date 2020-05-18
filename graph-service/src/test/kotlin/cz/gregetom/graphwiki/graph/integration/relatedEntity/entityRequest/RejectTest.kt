package cz.gregetom.graphwiki.graph.integration.relatedEntity.entityRequest

import cz.gregetom.graphwiki.api.graph.model.CompanyTO
import cz.gregetom.graphwiki.api.graph.model.EntityRequestState
import cz.gregetom.graphwiki.api.graph.model.EntityRequestTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.jpa.repository.EntityRequestRepository
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.web.CompanyApiLinks
import cz.gregetom.graphwiki.graph.web.EntityRequestApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class RejectTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var entityRequestRepository: EntityRequestRepository

    @Test
    fun rejectEntityRequestTest() {
        // create
        val createCompany = companyDataSupport.randomCreateCompanyTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(CompanyApiLinks.create().toUri(), createCompany)
        val savedEntityRequest = httpGet.doGet(EntityRequestTO::class, location)

        // assign
        entityRequestRepository.save(entityRequestRepository.getOne(savedEntityRequest.id).copy(assignee = TestUsers.ADMIN.username))
        val assignedEntityRequest = httpGet.doGet(EntityRequestTO::class, location)
        assertThat(assignedEntityRequest.links.approve).isNotNull
        assertThat(assignedEntityRequest.links.reject).isNotNull

        // reject and check
        this.expectTaskFinishing(assignedEntityRequest.links.task!!, TestUsers.ADMIN.username)
        httpPut.doPutEmptyBody(assignedEntityRequest.links.reject!!.href)
        val rejectedEntityRequest = httpGet.doGet(EntityRequestTO::class, location)
        assertThat(rejectedEntityRequest.state).isEqualTo(EntityRequestState.REJECTED)
        assertThat(rejectedEntityRequest.links.approve).isNull()
        assertThat(rejectedEntityRequest.links.reject).isNull()

        // check graph entity
        val graphEntity = httpGet.doGet(CompanyTO::class, rejectedEntityRequest.links.entity.href)
        assertThat(graphEntity.state).isEqualTo(GraphEntityState.REJECTED)
    }


    @Test
    fun rejectNotAssignedEntityRequestTest() {
        val createCompanyTO = companyDataSupport.randomCreateCompanyTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(CompanyApiLinks.create().toUri(), createCompanyTO)
        val savedEntityRequest = httpGet.doGet(EntityRequestTO::class, location)
        httpPut.doPutAndExpect(EntityRequestApiLinks.reject(savedEntityRequest.id).toUri(), HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
