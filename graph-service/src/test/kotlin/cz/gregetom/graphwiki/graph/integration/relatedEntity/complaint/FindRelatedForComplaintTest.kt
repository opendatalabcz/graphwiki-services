package cz.gregetom.graphwiki.graph.integration.relatedEntity.complaint

import cz.gregetom.graphwiki.api.graph.model.ComplaintTO
import cz.gregetom.graphwiki.api.graph.model.RelatedComplaintsTO
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.ComplaintDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.ComplaintApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class FindRelatedForComplaintTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var complaintDataSupport: ComplaintDataSupport

    @Test
    fun findRelatedComplaintsForEntityTest() {
        val dbPerson = personDataSupport.randomActivePerson()
        val dbComplaint = complaintDataSupport.randomCurrentComplaint(graphEntityId = dbPerson.id)
        val currentComplaint = complaintDataSupport.randomCurrentComplaint(graphEntityId = dbPerson.id)
        val historicComplaint = complaintDataSupport.randomHistoricComplaint(graphEntityId = dbPerson.id)

        val complaint = httpGet.doGet(ComplaintTO::class, ComplaintApiLinks.self(dbComplaint.id).toUri())
        val relatedComplaints = httpGet.doGet(RelatedComplaintsTO::class, complaint.links.related.href)

        assertThat(relatedComplaints.current).asList().hasSize(1)
        assertThat(relatedComplaints.current.first().id).isEqualTo(currentComplaint.id)
        assertThat(relatedComplaints.historic).asList().hasSize(1)
        assertThat(relatedComplaints.historic.first().id).isEqualTo(historicComplaint.id)
    }
}
