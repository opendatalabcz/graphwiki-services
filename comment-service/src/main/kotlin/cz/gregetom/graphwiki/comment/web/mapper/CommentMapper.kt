package cz.gregetom.graphwiki.comment.web.mapper

import cz.gregetom.graphwiki.api.comment.model.CommentTO
import cz.gregetom.graphwiki.api.comment.model.CommentTOLinks
import cz.gregetom.graphwiki.comment.dao.data.Comment
import cz.gregetom.graphwiki.comment.web.CommentApiLinks
import cz.gregetom.graphwiki.comment.web.LinkFactory
import cz.gregetom.graphwiki.comment.web.toLinkTO
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import org.springframework.stereotype.Component

@Component
class CommentMapper(private val linkFactory: LinkFactory) {

    fun map(comment: Comment, replyLevel: Int = 0): CommentTO {
        require(replyLevel >= 0) { "Reply level must be greater than 0, current value: $replyLevel" }
        return CommentTO(
                id = comment.id,
                author = linkFactory.userById(comment.author, "author").toLinkTO(),
                created = comment.created,
                text = comment.text,
                replies = comment.replies.map { this.map(it, replyLevel + 1) },
                links = prepareLinks(comment, replyLevel)
        )
    }

    private fun prepareLinks(comment: Comment, replyLevel: Int): CommentTOLinks {
        return CommentTOLinks(
                reply = if (replyLevel < MAX_REPLY_LEVEL) {
                    CommentApiLinks.reply(comment.id).toLinkTO()
                } else null,
                update = if (UserAccessor.currentUserIs(comment.author)) CommentApiLinks.update(comment.id).toLinkTO() else null,
                delete = if (comment.replies.isEmpty() && UserAccessor.currentUserIs(comment.author))
                    CommentApiLinks.delete(comment.id).toLinkTO() else null
        )
    }

    companion object {
        private const val MAX_REPLY_LEVEL = 10;
    }
}
