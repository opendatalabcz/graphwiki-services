package cz.gregetom.graphwiki.graph.integration.graphEntity.relationship

import cz.gregetom.graphwiki.api.graph.model.RelatedRelationshipsTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.support.data.RelationshipDataSupport
import cz.gregetom.graphwiki.graph.web.RelationshipApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

class FindRelatedForVertexTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var relationshipDataSupport: RelationshipDataSupport

    @Test
    @WithMockUser(authorities = [Roles.ROLE_ADMIN])  // only admin is allowed to create edge with inactive vertex,...
    fun findRelatedRelationshipsForVertexTest() {
        val entryPoint = personDataSupport.randomActivePerson()
        val activePerson = personDataSupport.randomActivePerson()
        val activeCompany = companyDataSupport.randomActiveCompany()
        val inactiveCompany = companyDataSupport.randomInactiveCompany()

        val outgoingActive = relationshipDataSupport.randomActiveRelationship(entryPoint.id, activePerson.id)
        relationshipDataSupport.randomActiveRelationship(entryPoint.id, inactiveCompany.id)

        val incomingActive = relationshipDataSupport.randomActiveRelationship(activeCompany.id, entryPoint.id)
        relationshipDataSupport.randomActiveRelationship(inactiveCompany.id, entryPoint.id)

        // get as user
        val userRelatedRelationships = httpGet.doGet(RelatedRelationshipsTO::class, RelationshipApiLinks.findRelatedForVertex(entryPoint.id).toUri(), TestUsers.USER)
        assertThat(userRelatedRelationships.incoming).asList().hasSize(1)
        assertThat(userRelatedRelationships.incoming.map { it.id }).asList().containsAll(listOf(incomingActive.id))
        assertThat(userRelatedRelationships.outgoing).asList().hasSize(1)
        assertThat(userRelatedRelationships.outgoing.map { it.id }).asList().containsAll(listOf(outgoingActive.id))

        // get as admin
        val adminRelatedRelationships = httpGet.doGet(RelatedRelationshipsTO::class, RelationshipApiLinks.findRelatedForVertex(entryPoint.id).toUri(), TestUsers.ADMIN)
        assertThat(adminRelatedRelationships.incoming).asList().hasSize(2)
        assertThat(adminRelatedRelationships.outgoing).asList().hasSize(2)
    }
}
