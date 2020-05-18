package cz.gregetom.graphwiki.graph.dao.framework.fulltextSearch

import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.repository.vertex.AbstractVertexRepository
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.springframework.data.domain.Pageable
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * Proxy which delegate fulltext search functions to [FulltextSearchProvider].
 * Other functions are invoked on the related repository class.
 *
 * @see AbstractVertexRepository
 */
class VertexFulltextSearchProxy<T : BaseVertex, U>(datasource: GraphTraversalSource, entityKClass: KClass<T>) : MethodInterceptor
        where U : AbstractVertexRepository<T>, U : VertexFulltextSearchApi<T> {

    private var fulltextSearchProvider = FulltextSearchProvider(entityKClass, BaseVertex.resolveVertexTypeFromKClass(entityKClass), datasource)


    override fun intercept(obj: Any, method: Method, args: Array<out Any>, proxy: MethodProxy): Any {
        @Suppress("UNCHECKED_CAST")
        return when (method.declaringClass) {
            Object::class.java -> proxy.invokeSuper(obj, args)
            VertexFulltextSearchApi::class.java -> this.invokeFulltextSearch(obj as U, method, args)
            else -> proxy.invokeSuper(obj, args)
        }
    }

    private fun invokeFulltextSearch(repository: U, method: Method, args: Array<out Any>): Any {
        return when (method.name) {
            VertexFulltextSearchApi<T>::fulltextSearch.name -> fulltextSearchProvider.fulltextSearch(args[0] as String, args[1] as Pageable).map { repository.findById(it.id().toString()) }
            VertexFulltextSearchApi<T>::fulltextSearchCount.name -> fulltextSearchProvider.fulltextSearchCount(args[0] as String)
            else -> throw IllegalStateException("Unknown method ${method.name}!")
        }
    }


    companion object {
        /**
         * Create fulltext search proxy for specific repository.
         *
         * @param datasource connection to JanusGraph database
         * @param entityKClass vertex KClass
         * @param repositoryKClass repository KClass
         * @return proxy instance
         */
        fun <T : BaseVertex, U> createProxy(datasource: GraphTraversalSource,
                                            entityKClass: KClass<T>,
                                            repositoryKClass: KClass<U>): U
                where U : AbstractVertexRepository<T>, U : VertexFulltextSearchApi<T> {

            val enhancer = Enhancer()
            enhancer.setSuperclass(repositoryKClass.java)
            enhancer.setCallback(VertexFulltextSearchProxy<T, U>(datasource, entityKClass))
            @Suppress("UNCHECKED_CAST")
            return enhancer.create() as U
        }
    }
}
