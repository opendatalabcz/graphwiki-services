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

class RejectTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var complaintRepository: ComplaintRepository
    @Autowired
    private lateinit var complaintDataSupport: ComplaintDataSupport

    @Test
    fun rejectComplaintTest() {
        // get setup
        val dbCompany = companyDataSupport.randomActiveCompany()
        val company = httpGet.doGet(CompanyTO::class, CompanyApiLinks.self(dbCompany.id).toUri())
        val setup = httpGet.doGet(ComplaintSetupTO::class, company.links.complaintCreate!!.href)

        // create
        val createComplaint = complaintDataSupport.randomCreateComplaintTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(setup.links.create.href, createComplaint)
        val complaint = httpGet.doGet(ComplaintTO::class, location)

        // assign
        complaintRepository.save(complaintRepository.getOne(complaint.id).copy(assignee = TestUsers.ADMIN.username))
        val assignedComplaint = httpGet.doGet(ComplaintTO::class, location)
        assertThat(assignedComplaint.links.approve).isNotNull
        assertThat(assignedComplaint.links.reject).isNotNull

        // reject
        this.expectTaskFinishing(assignedComplaint.links.task!!, TestUsers.ADMIN.username)
        httpPut.doPutEmptyBody(assignedComplaint.links.reject!!.href)

        // get and check
        val rejectedComplaint = httpGet.doGet(ComplaintTO::class, assignedComplaint.links.self.href)
        assertThat(rejectedComplaint.state).isEqualTo(ComplaintState.REJECTED)
        assertThat(rejectedComplaint.links.approve).isNull()
        assertThat(rejectedComplaint.links.reject).isNull()

        // check graph entity
        val graphEntity = httpGet.doGet(CompanyTO::class, rejectedComplaint.links.entity.href)
        assertThat(graphEntity.state).isEqualTo(GraphEntityState.ACTIVE)
        val graphEntityHistory = httpGet.doGet(Array<HistoryTO>::class, graphEntity.links.history.href)
        assertThat(graphEntityHistory.find { it.type === HistoryType.COMPLAINT_REJECTED }).isNotNull
    }

    @Test
    fun rejectNotAssignedComplaintTest() {
        val dbCompany = companyDataSupport.randomActiveCompany()
        val company = httpGet.doGet(CompanyTO::class, CompanyApiLinks.self(dbCompany.id).toUri())
        val setup = httpGet.doGet(ComplaintSetupTO::class, company.links.complaintCreate!!.href)

        val createComplaint = complaintDataSupport.randomCreateComplaintTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(setup.links.create.href, createComplaint)
        val complaint = httpGet.doGet(ComplaintTO::class, location)

        httpPut.doPutAndExpect(ComplaintApiLinks.reject(complaint.id).toUri(), HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
