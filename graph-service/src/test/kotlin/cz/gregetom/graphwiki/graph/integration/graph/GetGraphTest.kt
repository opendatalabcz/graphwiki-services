package cz.gregetom.graphwiki.graph.integration.graph

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.GraphTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphEntity
import cz.gregetom.graphwiki.graph.dao.gremlin.data.edge.Relationship
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.support.data.RelationshipDataSupport
import cz.gregetom.graphwiki.graph.web.GraphApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

class GetGraphTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var relationshipDataSupport: RelationshipDataSupport

    @Test
    fun getComplexGraphByVertexIdWithActiveElementsTest() {
        val entryPoint = personDataSupport.randomActivePerson()
        val person2 = personDataSupport.randomActivePerson()
        val company1 = companyDataSupport.randomActiveCompany()
        val company2 = companyDataSupport.randomActiveCompany()
        val company3 = companyDataSupport.randomActiveCompany()
        relationshipDataSupport.randomActiveRelationship(entryPoint.id, person2.id)
        relationshipDataSupport.randomActiveRelationship(entryPoint.id, company1.id)
        relationshipDataSupport.randomActiveRelationship(person2.id, company2.id)
        relationshipDataSupport.randomActiveRelationship(company2.id, company3.id)

        val graphTO = httpGet.doGet(GraphTO::class, GraphApiLinks.getGraph(entryPoint.id).toUri(), TestUsers.USER)

        assertThat(graphTO.nodes.size).isEqualTo(5)
        assertThat(graphTO.edges.size).isEqualTo(4)
        assertThat(graphTO.nodes.map { vertex -> vertex.id }).asList().contains(
                entryPoint.id, person2.id, company1.id, company2.id, company3.id
        )
        assertThat(graphTO.rootNode.id == entryPoint.id)
        assertThat(graphTO.links.exportGraphML).isNotNull
        assertThat(graphTO.links.exportClueMaker).isNotNull
        graphTO.nodes.filter { it.id != entryPoint.id }.forEach { assertThat(it.links.graph).isNotNull }
        assertThat(graphTO.edges.first().links.self).isNotNull
    }

    @Test
    @WithMockUser(authorities = [Roles.ROLE_ADMIN])  // only admin is allowed to create edge with inactive vertex,...
    fun getGraphWithPossibleNonActiveElementsTest() {
        // entry point should be active, otherwise 404 will be returned
        val entryPoint = personDataSupport.randomActivePerson()
        val person2 = personDataSupport.randomPerson()
        val person3 = personDataSupport.randomActivePerson()
        val person4 = personDataSupport.randomPerson()
        val person5 = personDataSupport.randomActivePerson()
        val company1 = companyDataSupport.randomActiveCompany()
        val company2 = companyDataSupport.randomActiveCompany()
        val company3 = companyDataSupport.randomCompany()
        val company4 = companyDataSupport.randomCompany()

        val vertices: List<GraphEntity> = listOf(entryPoint, person2, person3, person4, person5, company1, company2, company3, company4)
        val relationships = listOf(
                relationshipDataSupport.randomActiveRelationship(entryPoint.id, person2.id),
                relationshipDataSupport.randomRelationship(entryPoint, company1),
                relationshipDataSupport.randomActiveRelationship(entryPoint.id, company4.id),
                relationshipDataSupport.randomActiveRelationship(entryPoint.id, person5.id),
                relationshipDataSupport.randomActiveRelationship(person2.id, company2.id),
                relationshipDataSupport.randomRelationship(company2, company3),
                relationshipDataSupport.randomActiveRelationship(company3.id, company4.id),
                relationshipDataSupport.randomRelationship(person2, company4),
                relationshipDataSupport.randomActiveRelationship(person3.id, entryPoint.id),
                relationshipDataSupport.randomRelationship(person2, person5),
                relationshipDataSupport.randomActiveRelationship(person5.id, person4.id)
        )

        val activeVertexIds = vertices.filter { it.state === GraphEntityState.ACTIVE }.map { it.id }
        val activeRelationships = relationships.filter { it.state === GraphEntityState.ACTIVE }
        val connectedActiveVertexIds = mutableSetOf(entryPoint.id)
        val edgesBetweenActiveVertices = mutableSetOf<Relationship>()
        for (i in 0..activeRelationships.size - 1) {
            for (j in 0..activeRelationships.size - 1) {
                val currentRelationship = activeRelationships.get(j)
                if ((connectedActiveVertexIds.contains(currentRelationship.source) && activeVertexIds.contains(currentRelationship.target)) ||
                        (connectedActiveVertexIds.contains(currentRelationship.target) && activeVertexIds.contains(currentRelationship.source))) {
                    connectedActiveVertexIds.add(currentRelationship.source)
                    connectedActiveVertexIds.add(currentRelationship.target)
                    edgesBetweenActiveVertices.add(currentRelationship)
                }
            }
        }

        val userGraphTO = httpGet.doGet(GraphTO::class, GraphApiLinks.getGraph(entryPoint.id).toUri(), TestUsers.USER)

        assertThat(userGraphTO.nodes).asList().hasSize(connectedActiveVertexIds.size)
        assertThat(userGraphTO.edges).asList().hasSize(edgesBetweenActiveVertices.size)
        assertThat(userGraphTO.nodes.map { vertex -> vertex.id }).asList().containsAll(connectedActiveVertexIds)
        if (userGraphTO.nodes.size > 1) {
            assertThat(userGraphTO.links.exportGraphML).isNotNull
            assertThat(userGraphTO.links.exportClueMaker).isNotNull
        } else {
            assertThat(userGraphTO.links.exportGraphML).isNull()
            assertThat(userGraphTO.links.exportClueMaker).isNull()
        }


        val adminGraphTO = httpGet.doGet(GraphTO::class, GraphApiLinks.getGraph(entryPoint.id).toUri(), TestUsers.ADMIN)

        assertThat(adminGraphTO.nodes).asList().hasSize(vertices.size)
        assertThat(adminGraphTO.edges).asList().hasSize(relationships.size)
    }

    @Test
    fun getGraphWithSingleVertexTest() {
        val entity = personDataSupport.randomActivePerson()
        val graphTO = httpGet.doGet(GraphTO::class, GraphApiLinks.getGraph(entity.id).toUri(), TestUsers.USER)
        assertThat(graphTO.rootNode.id).isEqualTo(entity.id)
        assertThat(graphTO.rootNode.type).isNotNull
        assertThat(graphTO.rootNode.label).isNotNull()
        assertThat(graphTO.rootNode.links.self).isNotNull
        assertThat(graphTO.rootNode.links.graph).isNull()
        assertThat(graphTO.links.exportGraphML).isNull()
        assertThat(graphTO.links.exportClueMaker).isNull()
    }
}
