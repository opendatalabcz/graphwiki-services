package cz.gregetom.graphwiki.graph.integration.graphEntity.company

import cz.gregetom.graphwiki.api.graph.model.CompanyTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.web.CompanyApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class RestoreTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport

    @Test
    fun findNotActiveCompanyTest() {
        val inactiveCompany = companyDataSupport.randomInactiveCompany()
        httpPut.doPutEmptyBody(CompanyApiLinks.restore(inactiveCompany.id).toUri())
        val restoredCompany = httpGet.doGet(CompanyTO::class, CompanyApiLinks.self(inactiveCompany.id).toUri())
        assertThat(restoredCompany.state).isEqualTo(GraphEntityState.ACTIVE)

        // history record should be created
        this.historyStateTransitionRecordWasCreated(restoredCompany.id, inactiveCompany.state, GraphEntityState.ACTIVE)
    }
}
