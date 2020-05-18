package cz.gregetom.graphwiki.graph.integration.graphEntity.company

import cz.gregetom.graphwiki.api.graph.model.CompanyTO
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.web.CompanyApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

class FindTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport

    @Test
    fun findCompanyTest() {
        val expected = companyDataSupport.randomCompany()
        val actual = httpGet.doGet(CompanyTO::class, CompanyApiLinks.self(expected.id).toUri())
        assertThat(actual).isEqualToComparingOnlyGivenFields(expected,
                "id", "created", "state", "informationSource",
                "officialName", "registrationNumber", "industry", "inception", "informationSource")
        assertThat(actual.headquarters).isEqualToComparingOnlyGivenFields(expected.headquarters,
                "street", "houseNumber", "postalCode", "city", "country", "landRegistryNumber")
        assertThat(actual.informationSourceHost).isNotNull()
    }

    @Test
    fun companyHeadRequestTest() {
        val company = companyDataSupport.randomCompany()
        httpHead.doHead(
                CompanyApiLinks.self(company.id).toUri(), mapOf(HttpHeaders.CONTENT_TYPE to "application/vnd.cz.gregetom.graphwiki.company+json")
        )
    }

    @Test
    fun companyDataAvailabilityTest() {
        val activeCompany = companyDataSupport.randomActiveCompany()
        val inactiveCompany = companyDataSupport.randomInactiveCompany()

        // user
        httpGet.doGetAndExpect(CompanyApiLinks.self(activeCompany.id).toUri(), HttpStatus.OK, TestUsers.USER)
        httpGet.doGetAndExpect(CompanyApiLinks.self(inactiveCompany.id).toUri(), HttpStatus.NOT_FOUND, TestUsers.USER)

        // admin
        httpGet.doGetAndExpect(CompanyApiLinks.self(activeCompany.id).toUri(), HttpStatus.OK, TestUsers.ADMIN)
        httpGet.doGetAndExpect(CompanyApiLinks.self(inactiveCompany.id).toUri(), HttpStatus.OK, TestUsers.ADMIN)
    }
}
