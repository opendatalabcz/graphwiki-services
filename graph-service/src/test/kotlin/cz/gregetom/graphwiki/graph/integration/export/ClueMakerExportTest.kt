package cz.gregetom.graphwiki.graph.integration.export

import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.support.data.RelationshipDataSupport
import cz.gregetom.graphwiki.graph.web.ExportApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser

class ClueMakerExportTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var relationshipDataSupport: RelationshipDataSupport

    @Test
    fun clueMakerExportTest() {
        val entryPoint = personDataSupport.randomActivePerson()
        val person2 = personDataSupport.randomActivePerson()
        val company1 = companyDataSupport.randomActiveCompany()
        relationshipDataSupport.randomActiveRelationship(entryPoint.id, person2.id)
        relationshipDataSupport.randomActiveRelationship(entryPoint.id, company1.id)

        val response = httpGet.doGetAndReturnResponse(ExportApiLinks.clueMaker(entryPoint.id).toUri())

        assertThat(response.contentLength).isGreaterThan(0)
        assertThat(response.contentType).isEqualTo("application/zip")
        assertThat(response.getHeaderValue(HttpHeaders.CONTENT_DISPOSITION)).isNotNull
    }

    @Test
    @WithMockUser(authorities = [Roles.ROLE_ADMIN])  // only admin is allowed to create edge with inactive vertex,...
    fun clueMakerExportInactiveVertexTest() {
        val entryPoint = personDataSupport.randomInactivePerson()
        val person2 = personDataSupport.randomActivePerson()
        relationshipDataSupport.randomActiveRelationship(entryPoint.id, person2.id)

        httpGet.doGetAndExpect(ExportApiLinks.clueMaker(entryPoint.id).toUri(), HttpStatus.NOT_FOUND, TestUsers.USER)
        httpGet.doGetAndExpect(ExportApiLinks.clueMaker(entryPoint.id).toUri(), HttpStatus.OK, TestUsers.ADMIN)
    }
}
