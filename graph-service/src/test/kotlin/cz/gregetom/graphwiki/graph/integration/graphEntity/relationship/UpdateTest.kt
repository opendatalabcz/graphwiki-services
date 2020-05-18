package cz.gregetom.graphwiki.graph.integration.graphEntity.relationship

import cz.gregetom.graphwiki.api.graph.model.RelationshipTO
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.support.data.RelationshipDataSupport
import cz.gregetom.graphwiki.graph.web.RelationshipApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class UpdateTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var relationshipDataSupport: RelationshipDataSupport
    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport

    @Test
    fun updateRelationshipTest() {
        val dbSource = companyDataSupport.randomActiveCompany()
        val dbTarget = personDataSupport.randomActivePerson()
        val dbRelationship = relationshipDataSupport.randomRelationship(dbSource, dbTarget)
        val updateRelationshipTO = relationshipDataSupport.randomCreateRelationship(dbRelationship.source, dbRelationship.target)

        httpPut.doPut(RelationshipApiLinks.update(dbRelationship.id).toUri(), updateRelationshipTO)
        val relationshipTO = httpGet.doGet(RelationshipTO::class, RelationshipApiLinks.self(dbRelationship.id).toUri())

        assertThat(relationshipTO.id).isEqualTo(dbRelationship.id)
        assertThat(relationshipTO.created).isEqualTo(dbRelationship.created)
        assertThat(relationshipTO).isEqualToComparingOnlyGivenFields(updateRelationshipTO,
                "type", "informationSource", "description")
    }
}
