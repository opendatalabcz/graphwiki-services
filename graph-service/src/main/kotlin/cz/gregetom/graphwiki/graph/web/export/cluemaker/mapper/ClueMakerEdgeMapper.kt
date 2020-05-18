package cz.gregetom.graphwiki.graph.web.export.cluemaker.mapper

import cz.gregetom.graphwiki.graph.dao.framework.data.edge.EdgeType
import cz.gregetom.graphwiki.graph.dao.framework.repository.vertex.AbstractVertexRepository.Companion.NESTED_VERTEX_NAME_PROPERTY
import cz.gregetom.graphwiki.graph.dao.gremlin.data.edge.Relationship
import cz.gregetom.graphwiki.graph.web.export.cluemaker.data.ClueMakerEdge
import org.apache.tinkerpop.gremlin.structure.Edge
import org.janusgraph.graphdb.relations.RelationIdentifier

object ClueMakerEdgeMapper : AbstractClueMakerMapper() {

    fun map(edge: Edge): ClueMakerEdge {
        val type = EdgeType.valueOf(edge.label())

        return when (type) {
            EdgeType.RELATIONSHIP -> ClueMakerRelationshipMapper.map(edge, type)
            EdgeType.NESTED_VERTEX -> ClueMakerNestedVertexMapper.map(edge, type)
        }
    }

    private fun resolveClueMakerEdgeType(type: EdgeType): String {
        return when (type) {
            EdgeType.RELATIONSHIP -> EdgeType.RELATIONSHIP.name
            EdgeType.NESTED_VERTEX -> EdgeType.NESTED_VERTEX.name
        }
    }


    private object ClueMakerRelationshipMapper {
        fun map(edge: Edge, type: EdgeType): ClueMakerEdge {
            val propertyMap = getPropertyMap(edge)
            return ClueMakerEdge(
                    id = (edge.id() as RelationIdentifier).relationId,
                    label = propertyMap[Relationship::type.name]!!.toLowerCase(),
                    entity = resolveClueMakerEdgeType(type),
                    sourceId = edge.outVertex().id().toString().toLong(),
                    targetId = edge.inVertex().id().toString().toLong(),
                    attributes = propertyMap
            )
        }
    }

    private object ClueMakerNestedVertexMapper {
        fun map(edge: Edge, type: EdgeType): ClueMakerEdge {
            val propertyMap = getPropertyMap(edge)
            return ClueMakerEdge(
                    id = (edge.id() as RelationIdentifier).relationId,
                    label = propertyMap[NESTED_VERTEX_NAME_PROPERTY]!!.toLowerCase(),
                    entity = resolveClueMakerEdgeType(type),
                    sourceId = (edge.id() as RelationIdentifier).outVertexId,
                    targetId = (edge.id() as RelationIdentifier).inVertexId
            )
        }
    }
}
