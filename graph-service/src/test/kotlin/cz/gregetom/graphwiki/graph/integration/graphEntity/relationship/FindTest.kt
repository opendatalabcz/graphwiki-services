package cz.gregetom.graphwiki.graph.integration.graphEntity.relationship

import cz.gregetom.graphwiki.api.graph.model.RelationshipTO
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.support.data.RelationshipDataSupport
import cz.gregetom.graphwiki.graph.web.RelationshipApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class FindTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var relationshipDataSupport: RelationshipDataSupport

    @Test
    fun findRelationshipTest() {
        val source = personDataSupport.randomActivePerson()
        val target = personDataSupport.randomActivePerson()
        val expected = relationshipDataSupport.randomRelationship(source, target)
        val actual = httpGet.doGet(RelationshipTO::class, RelationshipApiLinks.self(expected.id).toUri())
        assertThat(actual).isEqualToComparingOnlyGivenFields(expected,
                "id", "created", "state", "informationSource", "type", "description")
        assertThat(actual.informationSourceHost).isNotNull()
        assertThat(actual.source.id).isEqualTo(source.id)
        assertThat(actual.target.id).isEqualTo(target.id)
    }

    @Test
    fun dataAvailabilityTest() {
        val source = personDataSupport.randomActivePerson()
        val target = personDataSupport.randomActivePerson()
        val activeRelationship = relationshipDataSupport.randomActiveRelationship(source.id, target.id)
        val inactiveRelationship = relationshipDataSupport.randomInactiveRelationship(source.id, target.id)

        // user
        httpGet.doGetAndExpect(RelationshipApiLinks.self(activeRelationship.id).toUri(), HttpStatus.OK, TestUsers.USER)
        httpGet.doGetAndExpect(RelationshipApiLinks.self(inactiveRelationship.id).toUri(), HttpStatus.NOT_FOUND, TestUsers.USER)

        // admin
        httpGet.doGetAndExpect(RelationshipApiLinks.self(activeRelationship.id).toUri(), HttpStatus.OK, TestUsers.ADMIN)
        httpGet.doGetAndExpect(RelationshipApiLinks.self(inactiveRelationship.id).toUri(), HttpStatus.OK, TestUsers.ADMIN)
    }
}
