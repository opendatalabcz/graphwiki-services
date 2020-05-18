package cz.gregetom.graphwiki.graph.integration.graphEntity.company

import cz.gregetom.graphwiki.api.graph.model.CompanyTO
import cz.gregetom.graphwiki.api.graph.model.EntityRequestTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.HistoryType
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.web.CompanyApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class CreateTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport

    @Test
    fun createCompanyTest() {
        // create
        val createCompanyTO = companyDataSupport.randomCreateCompanyTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(CompanyApiLinks.create().toUri(), createCompanyTO)

        // get
        val entityRequestTO = httpGet.doGet(EntityRequestTO::class, location)
        val companyTO = httpGet.doGet(CompanyTO::class, entityRequestTO.links.entity.href)

        // check
        assertThat(createCompanyTO).isEqualToComparingOnlyGivenFields(companyTO,
                "officialName", "registrationNumber", "industry", "inception", "informationSource")
        assertThat(createCompanyTO.headquarters).isEqualToComparingOnlyGivenFields(companyTO.headquarters,
                "street", "houseNumber", "postalCode", "city", "country", "landRegistryNumber")
        assertThat(companyTO.id).isNotNull()
        assertThat(companyTO.created).isNotNull()
        assertThat(companyTO.author).isNotNull
        assertThat(companyTO.informationSourceHost).isNotNull()
        assertThat(companyTO.state).isEqualTo(GraphEntityState.CONCEPT)
        assertThat(companyTO.links.update).isNotNull
        assertThat(companyTO.links.delete).isNotNull
        assertThat(companyTO.links.restore).isNull()
        assertThat(companyTO.links.complaintCreate).isNull()

        // history record should be created
        this.historyRelatedEntityRecordWasCreated(companyTO.id, entityRequestTO.id, HistoryType.ENTITY_REQUEST_CREATED)
    }

    @Test
    fun createCompanyByUserTest() {
        val createCompanyTO = companyDataSupport.randomCreateCompanyTO()
        this.expectTaskCreating()
        httpPost.doPostAndExpect(CompanyApiLinks.create().toUri(), createCompanyTO, HttpStatus.CREATED, TestUsers.USER)
    }
}
