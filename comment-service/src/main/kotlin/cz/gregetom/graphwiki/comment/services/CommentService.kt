package cz.gregetom.graphwiki.comment.services

import cz.gregetom.graphwiki.api.comment.model.CommentTO
import cz.gregetom.graphwiki.api.comment.model.CreateCommentTO
import cz.gregetom.graphwiki.comment.dao.data.Comment
import cz.gregetom.graphwiki.comment.dao.repository.CommentRepository
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime
import java.util.*

@Service
class CommentService(private val commentRepository: CommentRepository) {

    /**
     * Find all comment for related entity, e.g. complaint, entity-request,...
     *
     * @param entityId id of related entity
     * @return list of related comments
     */
    fun findAllByEntityId(entityId: String): List<Comment> {
        return commentRepository.findAllByEntityIdAndRootIsTrue(entityId).sortedByDescending { it.created }
    }

    /**
     * Create new comment for entity.
     *
     * @param entityId id of related entity, e.g. complaint, entity-request,...
     * @param createCommentTO new comment value
     * @return new comment id
     */
    fun create(entityId: String, createCommentTO: CreateCommentTO): String {
        LOGGER.info("Create comment for related entity with id $entityId, new comment: $createCommentTO")
        return commentRepository.save(
                Comment(
                        id = UUID.randomUUID().toString(),
                        entityId = entityId,
                        author = UserAccessor.currentUserIdOrThrow,
                        created = OffsetDateTime.now(),
                        root = true,
                        text = createCommentTO.text
                )
        ).id
    }

    /**
     * Add reply to comment.
     *
     * @param id id of parent comment
     * @param reply reply comment value
     * @return reply comment
     */
    fun reply(id: String, reply: CreateCommentTO): Comment {
        LOGGER.info("Add reply to comment with id $id, reply comment value $reply")
        val parent = commentRepository.getOne(id)
        parent.replies.add(
                commentRepository.save(
                        Comment(
                                id = UUID.randomUUID().toString(),
                                entityId = parent.entityId,
                                author = UserAccessor.currentUserIdOrThrow,
                                created = OffsetDateTime.now(),
                                root = false,
                                text = reply.text
                        )
                )
        )
        return parent
    }

    /**
     * Delete comment.
     *
     * @param id id of comment to be deleted
     * @throws ResponseStatusException if current user is not comment author
     */
    fun delete(id: String) {
        LOGGER.info("Delete comment with id $id")
        val comment = commentRepository.getOne(id)
        if (UserAccessor.currentUserIsNot(comment.author)) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot delete comment, you are not the author.")
        }
        return commentRepository.deleteById(id)
    }

    /**
     * Update comment.
     *
     * @param id id of comment to be updated
     * @param commentTO new comment value
     * @throws ResponseStatusException if current user is not comment author
     */
    fun update(id: String, commentTO: CommentTO) {
        LOGGER.info("Update comment with id $id, new value $commentTO")
        require(id == commentTO.id) { "Parameter 'commentId [${id}]' and id from comment entity [${commentTO.id}] must be the same!" }
        val comment = commentRepository.getOne(id)
        if (UserAccessor.currentUserIsNot(comment.author)) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot update comment, you are not the author.")
        }
        commentRepository.save(comment.copy(text = commentTO.text))
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CommentService::class.java)
    }
}
