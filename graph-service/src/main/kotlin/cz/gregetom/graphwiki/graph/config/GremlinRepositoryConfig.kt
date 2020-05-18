package cz.gregetom.graphwiki.graph.config

import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.fulltextSearch.VertexFulltextSearchApi
import cz.gregetom.graphwiki.graph.dao.framework.fulltextSearch.VertexFulltextSearchProxy
import cz.gregetom.graphwiki.graph.dao.framework.repository.vertex.AbstractVertexRepository
import cz.gregetom.graphwiki.graph.dao.framework.repository.vertex.VertexRepositoryApi
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Company
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Person
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.edge.RelationshipRepository
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex.AddressRepository
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex.CompanyRepository
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex.PersonRepository
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.reflect.KClass

/**
 * Configure gremlin repositories as Spring beans.
 */
@Configuration
class GremlinRepositoryConfig {

    @Bean
    fun relationshipRepository(): RelationshipRepository {
        return RelationshipRepository()
    }

    @Bean
    fun addressRepository(datasource: GraphTraversalSource): AddressRepository {
        return AddressRepository()
    }

    @Bean
    fun personFulltextSearchRepository(datasource: GraphTraversalSource): PersonRepository {
        return registerGremlinRepository(datasource, Person::class, PersonRepository::class)
    }

    @Bean
    fun companyFulltextSearchRepository(datasource: GraphTraversalSource): CompanyRepository {
        return registerGremlinRepository(datasource, Company::class, CompanyRepository::class)
    }

    /**
     * Create gremlin vertex repository with fulltext search support.
     *
     * @param datasource connection to JanusGraph database
     * @param entityKClass repository entity Kotlin class
     * @param repositoryKClass repository Kotlin class
     * @return instance of [repositoryKClass]
     */
    fun <T : BaseVertex, U> registerGremlinRepository(datasource: GraphTraversalSource,
                                                      entityKClass: KClass<T>,
                                                      repositoryKClass: KClass<U>): U
            where U : VertexRepositoryApi<T>, U : AbstractVertexRepository<T>, U : VertexFulltextSearchApi<T> {
        return VertexFulltextSearchProxy.createProxy(datasource, entityKClass, repositoryKClass)
    }
}
