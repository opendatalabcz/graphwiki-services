package cz.gregetom.graphwiki.graph.integration.graphEntity.company

import cz.gregetom.graphwiki.api.graph.model.CompanyTO
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.web.CompanyApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class UpdateTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport

    @Test
    fun updateCompanyTest() {
        val dbCompany = companyDataSupport.randomCompany()
        val updateCompanyTO = companyDataSupport.randomCreateCompanyTO()

        httpPut.doPut(CompanyApiLinks.update(dbCompany.id).toUri(), updateCompanyTO)
        val companyTO = httpGet.doGet(CompanyTO::class, CompanyApiLinks.self(dbCompany.id).toUri())

        assertThat(companyTO.id).isEqualTo(dbCompany.id)
        assertThat(companyTO.created).isEqualTo(dbCompany.created)
        assertThat(companyTO).isEqualToComparingOnlyGivenFields(updateCompanyTO,
                "officialName", "registrationNumber", "industry", "inception", "informationSource")
        assertThat(companyTO.headquarters).isEqualToComparingOnlyGivenFields(updateCompanyTO.headquarters,
                "street", "houseNumber", "postalCode", "city", "country", "landRegistryNumber")
    }
}
