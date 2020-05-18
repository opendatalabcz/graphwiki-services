package cz.gregetom.graphwiki.graph.integration.relatedEntity.complaint

import cz.gregetom.graphwiki.api.graph.model.*
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.jpa.repository.ComplaintRepository
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.ComplaintDataSupport
import cz.gregetom.graphwiki.graph.web.CompanyApiLinks
import cz.gregetom.graphwiki.graph.web.ComplaintApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class ApproveTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var complaintRepository: ComplaintRepository
    @Autowired
    private lateinit var complaintDataSupport: ComplaintDataSupport

    @Test
    fun approveComplaintTest() {
        // get setup
        val dbCompany = companyDataSupport.randomActiveCompany()
        val company = httpGet.doGet(CompanyTO::class, CompanyApiLinks.self(dbCompany.id).toUri())
        val setup = httpGet.doGet(ComplaintSetupTO::class, company.links.complaintCreate!!.href)

        // create
        val createComplaint = complaintDataSupport.randomCreateComplaintTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(setup.links.create.href, createComplaint)

        // get
        val complaint = httpGet.doGet(ComplaintTO::class, location)

        // assign
        complaintRepository.save(complaintRepository.getOne(complaint.id).copy(assignee = TestUsers.ADMIN.username))
        val assignedComplaint = httpGet.doGet(ComplaintTO::class, location)
        assertThat(assignedComplaint.links.approve).isNotNull
        assertThat(assignedComplaint.links.reject).isNotNull

        // create another complaints
        this.expectTaskCreating()
        val anotherCurrentComplaintTO = httpGet.doGet(ComplaintTO::class, httpPost.doPost(setup.links.create.href, createComplaint))
        val anotherHistoricComplaint = complaintDataSupport.randomHistoricComplaint(company.id) // historic complaint insert directly to database

        // approve and check
        this.expectTaskFinishing(anotherCurrentComplaintTO.links.task!!, TestUsers.ADMIN.username)
        this.expectTaskFinishing(assignedComplaint.links.task!!, TestUsers.ADMIN.username, reset = false)
        httpPut.doPutEmptyBody(assignedComplaint.links.approve!!.href)
        val approvedComplaint = httpGet.doGet(ComplaintTO::class, assignedComplaint.links.self.href)
        assertThat(approvedComplaint.state).isEqualTo(ComplaintState.APPROVED)
        assertThat(approvedComplaint.links.approve).isNull()
        assertThat(approvedComplaint.links.reject).isNull()

        // check another complaints
        assertThat(complaintRepository.getOne(anotherCurrentComplaintTO.id).state).isEqualTo(ComplaintState.ENTITY_MODIFIED)
        assertThat(complaintRepository.getOne(anotherHistoricComplaint.id).state).isEqualTo(anotherHistoricComplaint.state)

        // check graph entity
        val graphEntity = httpGet.doGet(CompanyTO::class, approvedComplaint.links.entity.href)
        assertThat(graphEntity.state).isEqualTo(GraphEntityState.REVOKED)
        val graphEntityHistory = httpGet.doGet(Array<HistoryTO>::class, graphEntity.links.history.href)
        assertThat(graphEntityHistory.find { it.type === HistoryType.COMPLAINT_APPROVED }).isNotNull

        // new complaint is not allowed now
        httpPost.doPostAndExpect(setup.links.create.href, createComplaint, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun approveNotAssignedComplaintTest() {
        // get setup
        val dbCompany = companyDataSupport.randomActiveCompany()
        val company = httpGet.doGet(CompanyTO::class, CompanyApiLinks.self(dbCompany.id).toUri())
        val setup = httpGet.doGet(ComplaintSetupTO::class, company.links.complaintCreate!!.href)

        // create
        val createComplaint = complaintDataSupport.randomCreateComplaintTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(setup.links.create.href, createComplaint)

        val complaint = httpGet.doGet(ComplaintTO::class, location)

        httpPut.doPutAndExpect(ComplaintApiLinks.approve(complaint.id).toUri(), HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
