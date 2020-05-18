package cz.gregetom.graphwiki.graph.integration.relatedEntity.complaint

import cz.gregetom.graphwiki.api.graph.model.*
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.jpa.repository.ComplaintRepository
import cz.gregetom.graphwiki.graph.support.data.ComplaintDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class CreateTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var complaintDataSupport: ComplaintDataSupport
    @Autowired
    private lateinit var complaintRepository: ComplaintRepository

    @Test
    fun createComplaintTest() {
        val dbPerson = personDataSupport.randomActivePerson()
        val person = httpGet.doGet(PersonTO::class, PersonApiLinks.self(dbPerson.id).toUri())
        assertThat(person.links.complaintCreate).isNotNull

        val setup = httpGet.doGet(ComplaintSetupTO::class, person.links.complaintCreate!!.href)
        assertThat(setup.links.create).isNotNull
        assertThat(setup.links.entity).isNotNull
        assertThat(setup.links.related).isNotNull

        val createComplaint = complaintDataSupport.randomCreateComplaintTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(setup.links.create.href, createComplaint)

        val complaint = httpGet.doGet(ComplaintTO::class, location)
        assertThat(complaint.title).isEqualTo(createComplaint.title)
        assertThat(complaint.explanation).isEqualTo(createComplaint.explanation)
        assertThat(complaint.id).isNotNull()
        assertThat(complaint.author).isNotNull
        assertThat(complaint.created).isNotNull()
        assertThat(complaint.state).isEqualTo(ComplaintState.ADMIN_DECISION)
        assertThat(complaint.assignee).isNull()
        assertThat(complaint.links.commentCreate).isNotNull
        assertThat(complaint.links.approve).isNull()
        assertThat(complaint.links.reject).isNull()

        this.historyRelatedEntityRecordWasCreated(person.id, complaint.id, HistoryType.COMPLAINT_CREATED)

        // ip address is not available on frontend
        assertThat(complaintRepository.findAll().first().ipAddress.canonicalHostName).isNotNull()
    }
}
