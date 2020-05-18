package cz.gregetom.graphwiki.graph.integration.graphEntity.person

import cz.gregetom.graphwiki.api.graph.model.*
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.jpa.repository.ComplaintRepository
import cz.gregetom.graphwiki.graph.support.data.ComplaintDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class DeleteTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var complaintDataSupport: ComplaintDataSupport
    @Autowired
    private lateinit var complaintRepository: ComplaintRepository

    @Test
    fun deleteActivePersonTest() {
        // create active person in database
        val dbPerson = personDataSupport.randomActivePerson()

        // create related complaints
        val currentComplaint = complaintDataSupport.randomCurrentComplaint(graphEntityId = dbPerson.id)
        val historicComplaint = complaintDataSupport.randomHistoricComplaint(graphEntityId = dbPerson.id)

        // get
        val personTO = httpGet.doGet(PersonTO::class, PersonApiLinks.self(dbPerson.id).toUri())

        // delete from location
        assertThat(personTO.links.delete).isNotNull
        this.expectTaskFinishing(currentComplaint.taskUrl!!, TestUsers.ADMIN.username)
        httpDelete.doDelete(personTO.links.delete!!.href)

        // should be deleted
        val deletedPersonTO = httpGet.doGet(PersonTO::class, personTO.links.self.href)
        assertThat(deletedPersonTO.state).isEqualTo(GraphEntityState.DELETED)

        // entity request state should be changed
        assertThat(complaintRepository.getOne(currentComplaint.id).state).isEqualTo(ComplaintState.ENTITY_MODIFIED)
        assertThat(complaintRepository.getOne(historicComplaint.id).state).isEqualTo(historicComplaint.state)

        // history record should be created
        this.historyStateTransitionRecordWasCreated(deletedPersonTO.id, personTO.state, GraphEntityState.DELETED)
    }

    @Test
    fun deleteConceptPersonTest() {
        // create
        val createPersonTO = personDataSupport.randomCreatePersonTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(PersonApiLinks.create().toUri(), createPersonTO)

        // get
        val entityRequestTO = httpGet.doGet(EntityRequestTO::class, location)
        val personTO = httpGet.doGet(PersonTO::class, entityRequestTO.links.entity.href)
        assertThat(personTO.state).isEqualTo(GraphEntityState.CONCEPT)

        // delete
        assertThat(personTO.links.delete).isNotNull
        this.expectTaskFinishing(entityRequestTO.links.task!!, TestUsers.ADMIN.username)
        httpDelete.doDelete(personTO.links.delete!!.href)

        // should be deleted
        val deletedPersonTO = httpGet.doGet(PersonTO::class, personTO.links.self.href)
        assertThat(deletedPersonTO.state).isEqualTo(GraphEntityState.DELETED)

        // entity request state should be changed
        val entityRequestAfterDelete = httpGet.doGet(EntityRequestTO::class, location)
        assertThat(entityRequestAfterDelete.state).isEqualTo(EntityRequestState.ENTITY_MODIFIED)

        // history record should be created
        this.historyStateTransitionRecordWasCreated(deletedPersonTO.id, GraphEntityState.CONCEPT, GraphEntityState.DELETED)
    }
}
