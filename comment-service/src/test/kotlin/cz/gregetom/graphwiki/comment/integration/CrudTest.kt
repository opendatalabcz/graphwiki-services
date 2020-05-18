package cz.gregetom.graphwiki.comment.integration

import cz.gregetom.graphwiki.api.comment.model.CommentTO
import cz.gregetom.graphwiki.api.comment.model.CreateCommentTO
import cz.gregetom.graphwiki.comment.AbstractIntegrationTest
import cz.gregetom.graphwiki.comment.dao.repository.CommentRepository
import cz.gregetom.graphwiki.comment.web.CommentApiLinks
import cz.gregetom.graphwiki.commons.test.RandomGenerator
import cz.gregetom.graphwiki.commons.test.TestUsers
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class CrudTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Test
    fun crudTest() {
        val entityId = RandomGenerator.randomString(30)

        httpGet.doGet(Array<CommentTO>::class, CommentApiLinks.list(entityId).toUri()).let { assertThat(it).isEmpty() }


        val commentText = RandomGenerator.instance.nextObject(String::class.java)
        val createCommentTO = CreateCommentTO(commentText)
        val commentsLocation = httpPost.doPost(CommentApiLinks.create(entityId).toUri(), createCommentTO)

        val comment = httpGet.doGet(Array<CommentTO>::class, commentsLocation).let {
            assertThat(it).hasSize(1)
            it.first()
        }
        assertThat(comment.id).isNotNull()
        assertThat(comment.author).isNotNull
        assertThat(comment.created).isNotNull()
        assertThat(comment.text).isEqualTo(commentText)
        assertThat(comment.replies).asList().isEmpty()
        assertThat(comment.links.reply).isNotNull
        assertThat(comment.links.update).isNotNull
        assertThat(comment.links.delete).isNotNull

        // ip address is not available on frontend
        assertThat(commentRepository.findAll().first().ipAddress.canonicalHostName).isNotNull()


        val newText = RandomGenerator.instance.nextObject(String::class.java)
        httpPut.doPutAndExpect(comment.links.update!!.href, comment.copy(text = newText), HttpStatus.UNPROCESSABLE_ENTITY, TestUsers.USER_ANOTHER)
        httpPut.doPut(comment.links.update!!.href, comment.copy(text = newText))


        val commentAfterUpdate = httpGet.doGet(Array<CommentTO>::class, commentsLocation).let {
            assertThat(it).hasSize(1)
            it.first()
        }
        assertThat(commentAfterUpdate).isEqualToIgnoringGivenFields(comment, "text")
        assertThat(commentAfterUpdate.text).isEqualTo(newText)

        httpDelete.doDeleteAndExpect(commentAfterUpdate.links.delete!!.href, HttpStatus.UNPROCESSABLE_ENTITY, TestUsers.USER_ANOTHER)
        httpDelete.doDelete(commentAfterUpdate.links.delete!!.href)
        httpGet.doGet(Array<CommentTO>::class, commentsLocation).let { assertThat(it).isEmpty() }
    }

    @Test
    fun createBadRequestTest() {
        val entityId = RandomGenerator.randomString(30)
        val tooLongTextCreateCommentTO = CreateCommentTO(RandomGenerator.randomString(500))
        httpPost.doPostAndExpect(CommentApiLinks.create(entityId).toUri(), tooLongTextCreateCommentTO, HttpStatus.BAD_REQUEST)
    }
}
