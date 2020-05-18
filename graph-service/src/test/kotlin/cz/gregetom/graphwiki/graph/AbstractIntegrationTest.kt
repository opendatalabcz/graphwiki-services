package cz.gregetom.graphwiki.graph

import com.fasterxml.jackson.databind.ObjectMapper
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.HistoryType
import cz.gregetom.graphwiki.api.graph.model.LinkTO
import cz.gregetom.graphwiki.commons.test.web.*
import cz.gregetom.graphwiki.commons.web.InterCommunicationRestTemplate
import cz.gregetom.graphwiki.graph.dao.jpa.repository.HistoryRepository
import cz.gregetom.graphwiki.graph.support.data.AbstractDataSupport
import cz.gregetom.graphwiki.graph.web.LinkFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [Application::class, TestConfig::class])
@AutoConfigureMockMvc
@Transactional
abstract class AbstractIntegrationTest : AbstractDataSupport() {

    @Autowired
    protected lateinit var mockMvc: MockMvc
    @Autowired
    protected lateinit var objectMapper: ObjectMapper
    @Autowired
    private lateinit var datasource: GraphTraversalSource
    @Autowired
    private lateinit var historyRepository: HistoryRepository
    @Autowired
    protected lateinit var restTemplate: InterCommunicationRestTemplate
    @Autowired
    protected lateinit var linkFactory: LinkFactory

    protected lateinit var mockServer: MockRestServiceServer
    protected lateinit var httpGet: HttpGet
    protected lateinit var httpPost: HttpPost
    protected lateinit var httpPut: HttpPut
    protected lateinit var httpDelete: HttpDelete
    protected lateinit var httpHead: HttpHead

    @Before
    fun setUp() {
        this.httpGet = HttpGet(mockMvc, objectMapper)
        this.httpPost = HttpPost(mockMvc, objectMapper)
        this.httpPut = HttpPut(mockMvc, objectMapper)
        this.httpDelete = HttpDelete(mockMvc)
        this.httpHead = HttpHead(mockMvc)

        this.mockServer = MockRestServiceServer.createServer(restTemplate)

        this.clearDatabase()
    }

    @After
    fun destroy() {
        this.clearDatabase()
    }

    private fun clearDatabase() {
        // drop all elements - transactions are managed with gremlin server itself
        this.datasource.V().drop().iterate()
        this.datasource.E().drop().iterate()
    }

    fun expectTaskCreating() {
        this.mockServer.verify()
        this.mockServer.reset()
        this.mockServer.expect(ExpectedCount.once(),
                MockRestRequestMatchers.requestTo(linkFactory.taskCreate().toUri()))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators
                        .withStatus(HttpStatus.CREATED)
                        .location(URI("http://test"))
                )
    }

    fun expectTaskFinishing(link: LinkTO, userId: String, reset: Boolean = true) {
        this.expectTaskFinishing(URI(link.href), userId, reset)
    }

    fun expectTaskFinishing(url: URI, userId: String, reset: Boolean = true) {
        if (reset) {
            mockServer.verify()
            mockServer.reset()
        }
        mockServer.expect(ExpectedCount.once(),
                MockRestRequestMatchers.requestTo("$url?userId=$userId"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.DELETE))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK))
    }

    fun historyStateTransitionRecordWasCreated(entityId: String, previousState: GraphEntityState, currentState: GraphEntityState) {
        val historyRecord = historyRepository.findAllByEntityId(entityId)
                .find { it.previousState === previousState && it.currentState === currentState }
        Assertions.assertThat(historyRecord).isNotNull
        Assertions.assertThat(historyRecord!!.type).isEqualTo(HistoryType.STATE_TRANSITION)
    }

    fun historyRelatedEntityRecordWasCreated(entityId: String, relatedEntityId: String, historyType: HistoryType) {
        val historyRecord = historyRepository.findAllByEntityId(entityId)
                .find { it.relatedEntityId == relatedEntityId }
        Assertions.assertThat(historyRecord).isNotNull
        Assertions.assertThat(historyRecord!!.type).isEqualTo(historyType)
    }
}
