package cz.gregetom.graphwiki.user

import com.fasterxml.jackson.databind.ObjectMapper
import cz.gregetom.graphwiki.commons.test.web.HttpGet
import cz.gregetom.graphwiki.commons.test.web.HttpPost
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Application::class, TestConfig::class])
@AutoConfigureMockMvc
@Transactional
abstract class AbstractIntegrationTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc
    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    protected lateinit var httpGet: HttpGet
    protected lateinit var httpPost: HttpPost

    @Before
    fun setUp() {
        this.httpGet = HttpGet(mockMvc, objectMapper)
        this.httpPost = HttpPost(mockMvc, objectMapper)
    }
}
