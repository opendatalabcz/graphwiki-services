package cz.gregetom.graphwiki.graph.dao.framework.repository.vertex

import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicReference

@Component
class VertexRepositoryProvider(private val applicationContext: ApplicationContext) {
    val repositories: AtomicReference<List<AbstractVertexRepository<BaseVertex>>> = AtomicReference(emptyList())

    /**
     * Get vertex repository, which support specific type.
     *
     * @param type vertex type
     * @throws IllegalArgumentException if count of repositories with [type] support is not exactly one
     * @return vertex repository
     */
    fun getRepositoryForType(type: VertexType): AbstractVertexRepository<BaseVertex> {
        repositories.get().ifEmpty { this.initRepositoriesFromContext() }

        val repositories = repositories.get().filter { it.support(type) }
        require(repositories.size == 1) { "Invalid count of provided repositories for $type, expected 1 - actual ${repositories.size}" }
        return repositories.first()
    }


    fun initRepositoriesFromContext() {
        @Suppress("UNCHECKED_CAST")
        repositories.set(
                applicationContext.getBeansOfType(AbstractVertexRepository::class.java)
                        .map { it.value } as List<AbstractVertexRepository<BaseVertex>>
        )
    }
}
