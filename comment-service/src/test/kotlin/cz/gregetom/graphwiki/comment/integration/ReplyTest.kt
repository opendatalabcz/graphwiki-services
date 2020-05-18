package cz.gregetom.graphwiki.comment.integration

import cz.gregetom.graphwiki.api.comment.model.CommentTO
import cz.gregetom.graphwiki.comment.AbstractIntegrationTest
import cz.gregetom.graphwiki.comment.support.data.CommentDataSupport
import cz.gregetom.graphwiki.comment.web.CommentApiLinks
import cz.gregetom.graphwiki.commons.test.RandomGenerator
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ReplyTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var commentDataSupport: CommentDataSupport

    @Test
    fun replyTest() {
        val entityId = RandomGenerator.randomString(30)

        httpGet.doGet(Array<CommentTO>::class, CommentApiLinks.list(entityId).toUri()).let { assertThat(it).isEmpty() }

        val baseCreateCommentTO = commentDataSupport.randomCreateCommentTO()
        val baseCommentAfterCreate = httpGet.doGet(
                clazz = Array<CommentTO>::class,
                url = httpPost.doPost(CommentApiLinks.create(entityId).toUri(), baseCreateCommentTO)
        ).first()

        val firstReplyCreateCommentTO = commentDataSupport.randomCreateCommentTO()
        httpPost.doPost(CommentApiLinks.reply(baseCommentAfterCreate.id).toUri(), firstReplyCreateCommentTO)

        val secondReplyCreateCommentTO = commentDataSupport.randomCreateCommentTO()
        val commentsLocation = httpPost.doPost(CommentApiLinks.reply(baseCommentAfterCreate.id).toUri(), secondReplyCreateCommentTO)

        val parentComment = httpGet.doGet(Array<CommentTO>::class, commentsLocation).let {
            assertThat(it).hasSize(1)
            it.first()
        }
        assertThat(parentComment.text).isEqualTo(baseCommentAfterCreate.text)
        assertThat(parentComment.replies).asList().hasSize(2)
        assertThat(parentComment.replies.toTypedArray())
                .isSortedAccordingTo { o1: CommentTO, o2: CommentTO -> o1.created.compareTo(o2.created) }
        parentComment.replies.first().let {
            assertThat(it.text).isEqualTo(firstReplyCreateCommentTO.text)
            assertThat(it.replies).asList().isEmpty()
        }
        parentComment.replies.last().let {
            assertThat(it.text).isEqualTo(secondReplyCreateCommentTO.text)
            assertThat(it.replies).asList().isEmpty()
        }
    }
}
