package cz.gregetom.graphwiki.graph.integration.relatedEntity.entityRequest

import cz.gregetom.graphwiki.api.graph.model.EntityRequestTO
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.EntityRequestDataSupport
import cz.gregetom.graphwiki.graph.web.EntityRequestApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class AssignTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var entityRequestDataSupport: EntityRequestDataSupport

    @Test
    fun assignEntityRequestTest() {
        // create in database
        val dbCompany = companyDataSupport.randomActiveCompany()
        val dbEntityRequest = entityRequestDataSupport.randomCurrentEntityRequest(dbCompany.id) { it.copy(assignee = null) }

        // get
        val selfLink = EntityRequestApiLinks.self(dbEntityRequest.id).toUri()
        val entityRequest = httpGet.doGet(EntityRequestTO::class, selfLink)
        assertThat(entityRequest.assignee).isNull()

        // assign
        httpPut.doPutEmptyBody(EntityRequestApiLinks.assign(entityRequest.id, TestUsers.ADMIN.username).toUri(), TestUsers.TECHNICAL)
        assertThat(httpGet.doGet(EntityRequestTO::class, selfLink).assignee).isNotNull

        // next assignment should fail
        httpPut.doPutAndExpect(EntityRequestApiLinks.assign(entityRequest.id, TestUsers.ADMIN_ANOTHER.username).toUri(),
                HttpStatus.UNPROCESSABLE_ENTITY, TestUsers.TECHNICAL)

        // unassign
        httpPut.doPutEmptyBody(EntityRequestApiLinks.assign(entityRequest.id, null).toUri(), TestUsers.TECHNICAL)
        assertThat(httpGet.doGet(EntityRequestTO::class, selfLink).assignee).isNull()
    }

    @Test
    fun assignEntityRequestInFinalStateTest() {
        val historicEntityRequest = entityRequestDataSupport.randomHistoricEntityRequest("id")
        httpPut.doPutAndExpect(EntityRequestApiLinks.assign(historicEntityRequest.id, TestUsers.ADMIN.username).toUri(),
                HttpStatus.UNPROCESSABLE_ENTITY, TestUsers.TECHNICAL)
    }
}
