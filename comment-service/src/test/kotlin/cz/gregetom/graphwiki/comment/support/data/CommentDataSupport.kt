package cz.gregetom.graphwiki.comment.support.data

import cz.gregetom.graphwiki.api.comment.model.CreateCommentTO
import cz.gregetom.graphwiki.commons.test.RandomGenerator
import org.springframework.stereotype.Component

@Component
class CommentDataSupport {

    fun randomCreateCommentTO(): CreateCommentTO {
        return RandomGenerator.instance.nextObject(CreateCommentTO::class.java)
    }
}
