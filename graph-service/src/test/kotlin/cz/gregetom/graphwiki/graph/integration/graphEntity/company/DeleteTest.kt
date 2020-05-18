package cz.gregetom.graphwiki.graph.integration.graphEntity.company

import cz.gregetom.graphwiki.api.graph.model.*
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.jpa.repository.ComplaintRepository
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.ComplaintDataSupport
import cz.gregetom.graphwiki.graph.web.CompanyApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class DeleteTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var complaintDataSupport: ComplaintDataSupport
    @Autowired
    private lateinit var complaintRepository: ComplaintRepository

    @Test
    fun deleteActiveCompanyTest() {
        // create active company in database
        val dbCompany = companyDataSupport.randomActiveCompany()

        // create related complaints
        val currentComplaint = complaintDataSupport.randomCurrentComplaint(graphEntityId = dbCompany.id)
        val historicComplaint = complaintDataSupport.randomHistoricComplaint(graphEntityId = dbCompany.id)

        // get
        val companyTO = httpGet.doGet(CompanyTO::class, CompanyApiLinks.self(dbCompany.id).toUri())

        // delete from location
        assertThat(companyTO.links.delete).isNotNull
        this.expectTaskFinishing(currentComplaint.taskUrl!!, TestUsers.ADMIN.username)
        httpDelete.doDelete(companyTO.links.delete!!.href)

        // should be deleted
        val deletedCompanyTO = httpGet.doGet(CompanyTO::class, companyTO.links.self.href)
        assertThat(deletedCompanyTO.state).isEqualTo(GraphEntityState.DELETED)

        // entity request state should be changed
        assertThat(complaintRepository.getOne(currentComplaint.id).state).isEqualTo(ComplaintState.ENTITY_MODIFIED)
        assertThat(complaintRepository.getOne(historicComplaint.id).state).isEqualTo(historicComplaint.state)

        // history record should be created
        this.historyStateTransitionRecordWasCreated(deletedCompanyTO.id, dbCompany.state, GraphEntityState.DELETED)
    }

    @Test
    fun deleteConceptCompanyTest() {
        // create
        val createCompanyTO = companyDataSupport.randomCreateCompanyTO()
        this.expectTaskCreating()
        val location = httpPost.doPost(CompanyApiLinks.create().toUri(), createCompanyTO)

        // get
        val entityRequestTO = httpGet.doGet(EntityRequestTO::class, location)
        val companyTO = httpGet.doGet(CompanyTO::class, entityRequestTO.links.entity.href)
        assertThat(companyTO.state).isEqualTo(GraphEntityState.CONCEPT)

        // delete
        assertThat(companyTO.links.delete).isNotNull
        this.expectTaskFinishing(entityRequestTO.links.task!!, TestUsers.ADMIN.username)
        httpDelete.doDelete(companyTO.links.delete!!.href)

        // should be deleted
        val deletedCompanyTO = httpGet.doGet(CompanyTO::class, companyTO.links.self.href)
        assertThat(deletedCompanyTO.state).isEqualTo(GraphEntityState.DELETED)

        // entity request state should be changed
        val entityRequestAfterDelete = httpGet.doGet(EntityRequestTO::class, location)
        assertThat(entityRequestAfterDelete.state).isEqualTo(EntityRequestState.ENTITY_MODIFIED)

        // history record should be created
        this.historyStateTransitionRecordWasCreated(deletedCompanyTO.id, GraphEntityState.CONCEPT, GraphEntityState.DELETED)
    }
}
