package cz.gregetom.graphwiki.graph.integration.relatedEntity.complaint

import cz.gregetom.graphwiki.api.graph.model.ComplaintTO
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.ComplaintDataSupport
import cz.gregetom.graphwiki.graph.web.ComplaintApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class AssignTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var complaintDataSupport: ComplaintDataSupport

    @Test
    fun assignComplaintTest() {
        // create in database
        val dbCompany = companyDataSupport.randomActiveCompany()
        val dbComplaint = complaintDataSupport.randomCurrentComplaint(dbCompany.id) { it.copy(assignee = null) }

        // get
        val selfLink = ComplaintApiLinks.self(dbComplaint.id).toUri()
        val complaint = httpGet.doGet(ComplaintTO::class, selfLink)
        assertThat(complaint.assignee).isNull()

        // assign
        httpPut.doPutEmptyBody(ComplaintApiLinks.assign(complaint.id, TestUsers.ADMIN.username).toUri(), TestUsers.TECHNICAL)
        assertThat(httpGet.doGet(ComplaintTO::class, selfLink).assignee).isNotNull

        // next assignment should fail
        httpPut.doPutAndExpect(ComplaintApiLinks.assign(complaint.id, TestUsers.ADMIN_ANOTHER.username).toUri(),
                HttpStatus.UNPROCESSABLE_ENTITY, TestUsers.TECHNICAL)

        // unassign
        httpPut.doPutEmptyBody(ComplaintApiLinks.assign(complaint.id).toUri(), TestUsers.TECHNICAL)
        assertThat(httpGet.doGet(ComplaintTO::class, selfLink).assignee).isNull()
    }

    @Test
    fun assignComplaintInFinalStateTest() {
        val historicComplaint = complaintDataSupport.randomHistoricComplaint("id")
        httpPut.doPutAndExpect(ComplaintApiLinks.assign(historicComplaint.id, TestUsers.ADMIN.username).toUri(),
                HttpStatus.UNPROCESSABLE_ENTITY, TestUsers.TECHNICAL)
    }
}
