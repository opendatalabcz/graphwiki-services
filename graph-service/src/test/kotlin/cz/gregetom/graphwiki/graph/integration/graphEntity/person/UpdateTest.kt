package cz.gregetom.graphwiki.graph.integration.graphEntity.person

import cz.gregetom.graphwiki.api.graph.model.PersonTO
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class UpdateTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport

    @Test
    fun updatePersonTest() {
        val dbPerson = personDataSupport.randomPerson()
        val updatePersonTO = personDataSupport.randomCreatePersonTO()

        httpPut.doPut(PersonApiLinks.update(dbPerson.id).toUri(), updatePersonTO)
        val personTO = httpGet.doGet(PersonTO::class, PersonApiLinks.self(dbPerson.id).toUri())

        assertThat(personTO.id).isEqualTo(dbPerson.id)
        assertThat(personTO.created).isEqualTo(dbPerson.created)
        assertThat(personTO).isEqualToComparingOnlyGivenFields(updatePersonTO,
                "givenName", "familyName", "gender", "nationality", "dateOfBirth", "occupation", "informationSource")
    }
}
