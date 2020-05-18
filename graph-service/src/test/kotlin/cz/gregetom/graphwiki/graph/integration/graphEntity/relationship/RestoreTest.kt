package cz.gregetom.graphwiki.graph.integration.graphEntity.relationship

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.RelationshipTO
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.support.data.RelationshipDataSupport
import cz.gregetom.graphwiki.graph.web.RelationshipApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class RestoreTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var relationshipDataSupport: RelationshipDataSupport

    @Test
    fun restoreRelationshipTest() {
        val source = personDataSupport.randomActivePerson()
        val target = personDataSupport.randomActivePerson()
        val inactiveRelationship = relationshipDataSupport.randomInactiveRelationship(source.id, target.id)
        httpPut.doPutEmptyBody(RelationshipApiLinks.restore(inactiveRelationship.id).toUri())
        val restoredRelationship = httpGet.doGet(RelationshipTO::class, RelationshipApiLinks.self(inactiveRelationship.id).toUri())
        assertThat(restoredRelationship.state).isEqualTo(GraphEntityState.ACTIVE)

        // history record should be created
        this.historyStateTransitionRecordWasCreated(restoredRelationship.id, inactiveRelationship.state, GraphEntityState.ACTIVE)
    }
}
