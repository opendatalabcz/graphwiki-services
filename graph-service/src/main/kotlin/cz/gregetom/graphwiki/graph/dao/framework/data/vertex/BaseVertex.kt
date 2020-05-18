package cz.gregetom.graphwiki.graph.dao.framework.data.vertex

import java.time.OffsetDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Parent for vertices
 */
interface BaseVertex {
    val id: String
    val author: String
    val created: OffsetDateTime

    fun <T> accept(visitor: VertexVisitor<T>): T

    companion object {
        // should be invoked only on application startup
        fun resolveVertexTypeFromKClass(kClass: KClass<out BaseVertex>): VertexType {
            return kClass.findAnnotation<cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation.Vertex>()?.type
                    ?: throw java.lang.IllegalStateException("No vertex type provided for class $kClass!")
        }
    }
}

enum class VertexType {
    PERSON,
    COMPANY,
    ADDRESS
}
