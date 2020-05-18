package cz.gregetom.graphwiki.comment.dao.repository

import cz.gregetom.graphwiki.comment.dao.data.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, String> {

    fun findAllByEntityIdAndRootIsTrue(entityId: String): List<Comment>
}
