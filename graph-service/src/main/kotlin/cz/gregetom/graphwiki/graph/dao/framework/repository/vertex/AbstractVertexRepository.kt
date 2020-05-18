package cz.gregetom.graphwiki.graph.dao.framework.repository.vertex

import cz.gregetom.graphwiki.graph.dao.framework.data.edge.EdgeType
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType
import cz.gregetom.graphwiki.graph.dao.framework.repository.AbstractGremlinRepository
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.findValidVertexById
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.nextOrThrow
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.`__`.unfold
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.`__`.valueMap
import org.apache.tinkerpop.gremlin.process.traversal.step.util.WithOptions
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import javax.annotation.PostConstruct
import kotlin.reflect.KClass

abstract class AbstractVertexRepository<T : BaseVertex>(private val kClass: KClass<T>)
    : AbstractGremlinRepository(), VertexRepositoryApi<T> {

    @Autowired
    private lateinit var vertexRepositoryProvider: VertexRepositoryProvider

    private lateinit var vertexType: VertexType
    private lateinit var nestedVerticesConfig: Set<NestedVerticesConfig>


    @PostConstruct
    fun setUp() {
        this.vertexType = BaseVertex.resolveVertexTypeFromKClass(kClass)
        this.nestedVerticesConfig = NestedVerticesConfigInitializer.setup(kClass)
    }

    override fun support(type: VertexType): Boolean {
        return this.vertexType === type
    }

    /**
     * ############################################################
     * ###################     SAVE SECTION     ###################
     * ############################################################
     */
    override fun save(entity: T): T {
        val id = saveAsTraversal(entity, vertexType, "vertex")
                .select<Vertex>("vertex")
                .next()
                .id().toString()
        // do not apply user policy..
        return this.findByIdWithoutUserPolicy(id)
    }

    override fun saveAsTraversal(entity: T, vertexType: VertexType, stepLabel: String): GraphTraversal<Vertex, Vertex> {
        return datasource
                .addV(vertexType.name)
                .`as`(stepLabel)
                .setPropertiesFromEntity(entity, nestedVerticesConfig.map { it.nestedVertexFieldName })
                .apply { nestedVerticesConfig.forEach { config -> createNestedVertex(this, config, entity) } }
    }

    private fun createNestedVertex(traversal: GraphTraversal<Vertex, Vertex>, nestedVertexConfig: NestedVerticesConfig, entity: T): GraphTraversal<Vertex, Vertex> {
        val repository = nestedVertexConfig.getRepository(vertexRepositoryProvider)
        val nestedVertexTraversal = repository.saveAsTraversal(
                entity = nestedVertexConfig.getter.call(entity),
                vertexType = nestedVertexConfig.vertexType,
                stepLabel = nestedVertexConfig.nestedVertexEdgeName
        )

        return traversal
                .addE(EdgeType.NESTED_VERTEX.name)
                .property(NESTED_VERTEX_NAME_PROPERTY, nestedVertexConfig.nestedVertexEdgeName)
                .to(nestedVertexTraversal)
                .outV()
    }


    /**
     * ############################################################
     * ###################    UPDATE SECTION    ###################
     * ############################################################
     */
    override fun update(entity: T): T {
        val traversal = getVertexOfCurrentTypeTraversalById(entity.id)
        updateAsTraversal(entity, traversal).nextOrThrow(kClass, entity.id)
        return findById(entity.id)
    }

    override fun updateAsTraversal(entity: T, traversal: GraphTraversal<Vertex, Vertex>): GraphTraversal<Vertex, Vertex> {
        traversal.setPropertiesFromEntity(entity, nestedVerticesConfig.map { it.nestedVertexFieldName })
        nestedVerticesConfig.forEach { config ->
            val repository = config.getRepository(vertexRepositoryProvider)
            repository.updateAsTraversal(config.getter.call(entity), getNestedVertexEdge(traversal, config))
        }
        return traversal
    }

    private fun getNestedVertexEdge(traversal: GraphTraversal<Vertex, Vertex>, nestedVertexConfig: NestedVerticesConfig): GraphTraversal<Vertex, Vertex> {
        return traversal
                .outE(EdgeType.NESTED_VERTEX.name)
                .has(NESTED_VERTEX_NAME_PROPERTY, nestedVertexConfig.nestedVertexEdgeName)
                .inV()
                .`as`(nestedVertexConfig.nestedVertexEdgeName)
    }


    /**
     * ############################################################
     * ###################     FIND SECTION     ###################
     * ############################################################
     */
    override fun findById(id: String): T {
        return this.findByIdFromTraversal(id, getVertexOfCurrentTypeTraversalById(id))
    }

    private fun findByIdWithoutUserPolicy(id: String): T {
        return this.findByIdFromTraversal(id, datasource.V(id))
    }

    private fun findByIdFromTraversal(id: String, traversal: GraphTraversal<Vertex, Vertex>): T {
        // select vertex by id
        traversal.`as`("vertex")
        // resolve nested vertices
        nestedVerticesConfig.forEach { config -> getNestedVertexEdge(traversal, config) }
        // select step - select all vertices (vertex by id + nested vertices)
        val dataMap = traversal
                .select<LinkedHashMap<String, Any>>("vertex", "vertex",
                        *nestedVerticesConfig.map { it.nestedVertexEdgeName }.toTypedArray())
                .by(valueMap<Vertex, String>().with(WithOptions.tokens).by(unfold<String>()))
                .nextOrThrow(kClass, id)

        // modify map to allow deserialization
        val vertex = dataMap["vertex"]!!
        nestedVerticesConfig.forEach { config -> vertex[config.nestedVertexFieldName] = dataMap[config.nestedVertexEdgeName]!! }

        return objectMapper.convertValue(vertex, kClass.java)
    }

    private fun getVertexOfCurrentTypeTraversalById(id: String): GraphTraversal<Vertex, Vertex> {
        return datasource.findValidVertexById(id).hasLabel(vertexType.name)
    }

    companion object {
        val NESTED_VERTEX_NAME_PROPERTY = "nested-vertex-name"
    }
}
