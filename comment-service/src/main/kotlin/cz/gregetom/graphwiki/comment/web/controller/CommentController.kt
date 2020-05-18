package cz.gregetom.graphwiki.comment.web.controller

import cz.gregetom.graphwiki.api.comment.api.CommentApi
import cz.gregetom.graphwiki.api.comment.model.CommentTO
import cz.gregetom.graphwiki.api.comment.model.CreateCommentTO
import cz.gregetom.graphwiki.comment.services.CommentService
import cz.gregetom.graphwiki.comment.web.CommentApiLinks
import cz.gregetom.graphwiki.comment.web.mapper.CommentMapper
import cz.gregetom.graphwiki.commons.security.enums.Roles
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping
@Validated
class CommentController(
        private val commentMapper: CommentMapper,
        private val commentService: CommentService
) : CommentApi {

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional
    override fun create(@NotNull @Size(max = 50) @RequestParam entityId: String,
                        @Valid @RequestBody createCommentTO: CreateCommentTO): ResponseEntity<Unit> {
        commentService.create(entityId, createCommentTO)
        return ResponseEntity
                .created(CommentApiLinks.list(entityId).toUri())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION)
                .build()
    }

    @Transactional(readOnly = true)
    override fun findAllByEntityId(@NotNull @Size(max = 50) @RequestParam entityId: String): ResponseEntity<List<CommentTO>> {
        return ResponseEntity.ok(commentService.findAllByEntityId(entityId).map { commentMapper.map(it) })
    }

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional
    override fun reply(@Size(max = 50) @PathVariable id: String,
                       @Valid @RequestBody createCommentTO: CreateCommentTO): ResponseEntity<Unit> {
        val reply = commentService.reply(id, createCommentTO)
        return ResponseEntity
                .created(CommentApiLinks.list(reply.entityId).toUri())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION)
                .build()
    }

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional
    override fun delete(@Size(max = 50) @PathVariable id: String): ResponseEntity<Unit> {
        return ResponseEntity.ok(commentService.delete(id))
    }

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional
    override fun update(@Size(max = 50) @PathVariable id: String,
                        @Valid @RequestBody commentTO: CommentTO): ResponseEntity<Unit> {
        return ResponseEntity.ok(commentService.update(id, commentTO))
    }
}
