package cz.gregetom.graphwiki.graph.integration.relatedEntity.complaint

import cz.gregetom.graphwiki.api.graph.model.PersonTO
import cz.gregetom.graphwiki.api.graph.model.RelatedComplaintsTO
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.ComplaintDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class FindRelatedForEntityTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var complaintDataSupport: ComplaintDataSupport

    @Test
    fun findRelatedComplaintsForEntityTest() {
        val dbPerson = personDataSupport.randomActivePerson()
        val currentComplaint = complaintDataSupport.randomCurrentComplaint(dbPerson.id)
        val historicComplaint = complaintDataSupport.randomHistoricComplaint(dbPerson.id)

        val person = httpGet.doGet(PersonTO::class, PersonApiLinks.self(dbPerson.id).toUri())
        val relatedComplaints = httpGet.doGet(RelatedComplaintsTO::class, person.links.complaintList.href)

        assertThat(relatedComplaints.current).asList().hasSize(1)
        assertThat(relatedComplaints.current.first().id).isEqualTo(currentComplaint.id)
        assertThat(relatedComplaints.historic).asList().hasSize(1)
        assertThat(relatedComplaints.historic.first().id).isEqualTo(historicComplaint.id)
    }
}
