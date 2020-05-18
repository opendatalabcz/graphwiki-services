package cz.gregetom.graphwiki.graph.services.graphEntity

import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphEntity
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicReference

@Component
class GraphEntityServiceProvider(private val applicationContext: ApplicationContext) {
    val services: AtomicReference<List<AbstractGraphEntityService<out GraphEntity>>> = AtomicReference(emptyList())

    /**
     * Get graph entity service, which support [type].
     *
     * @param type graph entity type
     * @throws IllegalArgumentException if count of repositories with [type] support is not exactly one
     * @return graph entity repository with [type] support
     */
    fun getServiceForType(type: GraphEntityType): AbstractGraphEntityService<GraphEntity> {
        services.get().ifEmpty { this.initRepositoriesFromContext() }

        val repositories = services.get().filter { it.support(type) }
        require(repositories.size == 1) { "Invalid count of provided services for $type, expected 1 - actual ${repositories.size}" }
        @Suppress("UNCHECKED_CAST")
        return repositories.first() as AbstractGraphEntityService<GraphEntity>
    }


    fun initRepositoriesFromContext() {
        services.set(applicationContext.getBeansOfType(AbstractGraphEntityService::class.java).map { it.value })
    }
}
