package cz.gregetom.graphwiki.graph.integration.graphEntity.relationship

import cz.gregetom.graphwiki.api.graph.model.*
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.jpa.repository.ComplaintRepository
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.ComplaintDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.support.data.RelationshipDataSupport
import cz.gregetom.graphwiki.graph.web.RelationshipApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class DeleteTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var relationshipDataSupport: RelationshipDataSupport
    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var complaintDataSupport: ComplaintDataSupport
    @Autowired
    private lateinit var complaintRepository: ComplaintRepository

    @Test
    fun deleteActiveRelationshipTest() {
        // create active relationship in database
        val dbSource = personDataSupport.randomActivePerson()
        val dbTarget = companyDataSupport.randomActiveCompany()
        val dbRelationship = relationshipDataSupport.randomActiveRelationship(dbSource.id, dbTarget.id)

        // create related complaints
        val currentComplaint = complaintDataSupport.randomCurrentComplaint(graphEntityId = dbRelationship.id)
        val historicComplaint = complaintDataSupport.randomHistoricComplaint(graphEntityId = dbRelationship.id)

        // get
        val relationshipTO = httpGet.doGet(RelationshipTO::class, RelationshipApiLinks.self(dbRelationship.id).toUri())

        // delete from location
        assertThat(relationshipTO.links.delete).isNotNull
        this.expectTaskFinishing(currentComplaint.taskUrl!!, TestUsers.ADMIN.username)
        httpDelete.doDelete(relationshipTO.links.delete!!.href)

        // should be deleted
        val deletedRelationshipTO = httpGet.doGet(RelationshipTO::class, relationshipTO.links.self.href)
        assertThat(deletedRelationshipTO.state).isEqualTo(GraphEntityState.DELETED)

        // entity request state should be changed
        assertThat(complaintRepository.getOne(currentComplaint.id).state).isEqualTo(ComplaintState.ENTITY_MODIFIED)
        assertThat(complaintRepository.getOne(historicComplaint.id).state).isEqualTo(historicComplaint.state)

        // history record should be created
        this.historyStateTransitionRecordWasCreated(deletedRelationshipTO.id, relationshipTO.state, GraphEntityState.DELETED)
    }

    @Test
    fun deleteConceptRelationshipTest() {
        // create
        val source = personDataSupport.randomActivePerson()
        val target = companyDataSupport.randomActiveCompany()
        val createRelationshipTO = relationshipDataSupport.randomCreateRelationship(source.id, target.id)
        this.expectTaskCreating()
        val location = httpPost.doPost(RelationshipApiLinks.create().toUri(), createRelationshipTO)

        // get
        val entityRequestTO = httpGet.doGet(EntityRequestTO::class, location)
        val relationshipTO = httpGet.doGet(RelationshipTO::class, entityRequestTO.links.entity.href)
        assertThat(relationshipTO.state).isEqualTo(GraphEntityState.CONCEPT)

        // delete
        assertThat(relationshipTO.links.delete).isNotNull
        this.expectTaskFinishing(entityRequestTO.links.task!!, TestUsers.ADMIN.username)
        httpDelete.doDelete(relationshipTO.links.delete!!.href)

        // should be deleted
        val deletedRelationshipTO = httpGet.doGet(RelationshipTO::class, relationshipTO.links.self.href)
        assertThat(deletedRelationshipTO.state).isEqualTo(GraphEntityState.DELETED)

        // entity request state should be changed
        val entityRequestAfterDelete = httpGet.doGet(EntityRequestTO::class, location)
        assertThat(entityRequestAfterDelete.state).isEqualTo(EntityRequestState.ENTITY_MODIFIED)

        // history record should be created
        this.historyStateTransitionRecordWasCreated(deletedRelationshipTO.id, GraphEntityState.CONCEPT, GraphEntityState.DELETED)
    }
}
