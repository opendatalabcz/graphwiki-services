package cz.gregetom.graphwiki.graph.integration.graph

import cz.gregetom.graphwiki.api.graph.model.VertexTO
import cz.gregetom.graphwiki.api.graph.model.VertexType
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.GraphApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class FindVertexByIdTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var personDataSupport: PersonDataSupport

    @Test
    fun findVertexByIdTest() {
        val entity = personDataSupport.randomPerson()
        val vertex = httpGet.doGet(VertexTO::class, GraphApiLinks.findVertexById(entity.id).toUri())
        assertThat(vertex.id).isNotNull()
        assertThat(vertex.label).isNotNull()
        assertThat(vertex.type).isEqualTo(VertexType.PERSON)
        assertThat(vertex.links.self).isNotNull
        assertThat(vertex.links.graph).isNotNull
    }

    @Test
    fun findNotActiveVertexByIdTest() {
        val entity = companyDataSupport.randomInactiveCompany()
        httpGet.doGetAndExpect(GraphApiLinks.findVertexById(entity.id).toUri(), HttpStatus.NOT_FOUND, TestUsers.USER)
        httpGet.doGetAndExpect(GraphApiLinks.findVertexById(entity.id).toUri(), HttpStatus.OK, TestUsers.ADMIN)
    }

    @Test
    fun findNotExistingVertexByIdTest() {
        httpGet.doGetAndExpect(GraphApiLinks.findVertexById("tmp").toUri(), HttpStatus.NOT_FOUND, TestUsers.USER)
        httpGet.doGetAndExpect(GraphApiLinks.findVertexById("tmp").toUri(), HttpStatus.NOT_FOUND, TestUsers.ADMIN)
    }
}
