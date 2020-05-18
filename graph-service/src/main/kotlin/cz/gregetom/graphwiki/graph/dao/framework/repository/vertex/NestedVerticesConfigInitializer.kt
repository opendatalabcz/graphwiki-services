package cz.gregetom.graphwiki.graph.dao.framework.repository.vertex

import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation.NestedVertex
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

data class NestedVerticesConfig(
        val getter: KProperty1.Getter<out BaseVertex, BaseVertex>,
        val nestedVertexFieldName: String,
        val nestedVertexEdgeName: String,
        val vertexType: VertexType
) {
    fun getRepository(vertexRepositoryProvider: VertexRepositoryProvider): AbstractVertexRepository<BaseVertex> {
        return vertexRepositoryProvider.getRepositoryForType(this.vertexType)
    }
}

object NestedVerticesConfigInitializer {

    /**
     * Find out all nested vertices for specific [kClass] and save significant information about every vertex.
     *
     * @param kClass kClass
     */
    fun setup(kClass: KClass<out BaseVertex>): Set<NestedVerticesConfig> {
        val nestedVertexProperties = kClass.declaredMemberProperties.filter { it.findAnnotation<NestedVertex>() !== null }
        nestedVertexProperties.forEach { (it.returnType.classifier as KClass<*>).isSubclassOf(BaseVertex::class) }

        return nestedVertexProperties.map { nestedVertexProperty ->
            val vertexType = (nestedVertexProperty.returnType.classifier as KAnnotatedElement)
                    .findAnnotation<cz.gregetom.graphwiki.graph.dao.framework.data.vertex.annotation.Vertex>()
                    ?.type
                    ?: throw IllegalStateException("Vertex ${nestedVertexProperty.returnType} must have type!")

            @Suppress("UNCHECKED_CAST")
            NestedVerticesConfig(
                    // warning: only non nullable values are supported right now
                    getter = nestedVertexProperty.getter as KProperty1.Getter<out BaseVertex, BaseVertex>,
                    nestedVertexFieldName = nestedVertexProperty.name,
                    nestedVertexEdgeName = nestedVertexProperty.findAnnotation<NestedVertex>()!!.name,
                    vertexType = vertexType
            )
        }.toSet()
    }
}
