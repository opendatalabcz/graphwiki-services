package cz.gregetom.graphwiki.graph.integration.graphEntity.relationship

import cz.gregetom.graphwiki.api.graph.model.EntityRequestTO
import cz.gregetom.graphwiki.api.graph.model.HistoryType
import cz.gregetom.graphwiki.api.graph.model.RelationshipTO
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.support.data.RelationshipDataSupport
import cz.gregetom.graphwiki.graph.web.RelationshipApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class CreateTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var relationshipDataSupport: RelationshipDataSupport

    @Test
    fun createRelationshipTest() {
        val person1 = personDataSupport.randomActivePerson()
        val person2 = personDataSupport.randomActivePerson()
        val company1 = companyDataSupport.randomActiveCompany()
        val company2 = companyDataSupport.randomActiveCompany()

        createAndFindParametrizedTest(person1, person2)
        createAndFindParametrizedTest(person1, company1)
        createAndFindParametrizedTest(company1, company2)
    }

    private fun createAndFindParametrizedTest(source: BaseVertex, target: BaseVertex) {
        // create
        val createRelationshipTO = relationshipDataSupport.randomCreateRelationship(source.id, target.id)
        this.expectTaskCreating()
        val location = httpPost.doPost(RelationshipApiLinks.create().toUri(), createRelationshipTO)

        // get
        val entityRequestTO = httpGet.doGet(EntityRequestTO::class, location)
        val relationshipTO = httpGet.doGet(RelationshipTO::class, entityRequestTO.links.entity.href)

        // check
        assertThat(relationshipTO.id).isNotNull()
        assertThat(relationshipTO.created).isNotNull()
        assertThat(relationshipTO.author).isNotNull
        assertThat(relationshipTO.informationSource).isEqualTo(createRelationshipTO.informationSource)
        assertThat(relationshipTO.description).isEqualTo(createRelationshipTO.description)
        assertThat(relationshipTO.type).isEqualTo(createRelationshipTO.type)
        assertThat(relationshipTO.source).isNotNull
        assertThat(relationshipTO.target).isNotNull
        assertThat(relationshipTO.links.complaintCreate).isNull()

        // history record should be created
        this.historyRelatedEntityRecordWasCreated(relationshipTO.id, entityRequestTO.id, HistoryType.ENTITY_REQUEST_CREATED)
    }

    @Test
    fun createWithSameSourceAndTarget() {
        val person = personDataSupport.randomPerson()
        val createRelationshipTO = relationshipDataSupport.randomCreateRelationship(person.id, person.id)
        httpPost.doPostAndExpect(RelationshipApiLinks.create().toUri(), createRelationshipTO, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun createWithNotActiveVertexTest() {
        val activePerson = personDataSupport.randomActivePerson()
        val inactivePerson = personDataSupport.randomInactivePerson()
        val createRelationshipTO = relationshipDataSupport.randomCreateRelationship(activePerson.id, inactivePerson.id)

        // user
        httpPost.doPostAndExpect(RelationshipApiLinks.create().toUri(), createRelationshipTO, HttpStatus.NOT_FOUND, TestUsers.USER)

        // admin
        this.expectTaskCreating()
        httpPost.doPostAndExpect(RelationshipApiLinks.create().toUri(), createRelationshipTO, HttpStatus.CREATED, TestUsers.ADMIN)
    }

    @Test
    fun createPersonByUserTest() {
        val source = personDataSupport.randomActivePerson()
        val target = personDataSupport.randomActivePerson()
        val createRelationshipTO = relationshipDataSupport.randomCreateRelationship(source.id, target.id)
        this.expectTaskCreating()
        httpPost.doPostAndExpect(RelationshipApiLinks.create().toUri(), createRelationshipTO, HttpStatus.CREATED, TestUsers.USER)
    }
}
