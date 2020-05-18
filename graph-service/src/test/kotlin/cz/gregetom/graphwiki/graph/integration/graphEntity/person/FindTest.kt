package cz.gregetom.graphwiki.graph.integration.graphEntity.person

import cz.gregetom.graphwiki.api.graph.model.PersonTO
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

class FindTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport

    @Test
    fun findPersonTest() {
        val expected = personDataSupport.randomPerson()
        val actual = httpGet.doGet(PersonTO::class, PersonApiLinks.self(expected.id).toUri())
        assertThat(actual).isEqualToComparingOnlyGivenFields(expected,
                "id", "created", "state", "informationSource", "givenName", "familyName",
                "gender", "nationality", "dateOfBirth", "occupation")
        assertThat(actual.informationSourceHost).isNotNull()
    }

    @Test
    fun personHeadRequestTest() {
        val person = personDataSupport.randomPerson()
        httpHead.doHead(
                PersonApiLinks.self(person.id).toUri(), mapOf(HttpHeaders.CONTENT_TYPE to "application/vnd.cz.gregetom.graphwiki.person+json")
        )
    }

    @Test
    fun personDataAvailabilityTest() {
        val activePerson = personDataSupport.randomActivePerson()
        val inactivePerson = personDataSupport.randomInactivePerson()

        // user
        httpGet.doGetAndExpect(PersonApiLinks.self(activePerson.id).toUri(), HttpStatus.OK, TestUsers.USER)
        httpGet.doGetAndExpect(PersonApiLinks.self(inactivePerson.id).toUri(), HttpStatus.NOT_FOUND, TestUsers.USER)

        // admin
        httpGet.doGetAndExpect(PersonApiLinks.self(activePerson.id).toUri(), HttpStatus.OK, TestUsers.ADMIN)
        httpGet.doGetAndExpect(PersonApiLinks.self(inactivePerson.id).toUri(), HttpStatus.OK, TestUsers.ADMIN)
    }
}
