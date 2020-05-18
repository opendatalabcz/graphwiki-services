package cz.gregetom.graphwiki.graph.support.data

import cz.gregetom.graphwiki.api.graph.model.CreateRelationshipTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.RelationshipType
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.gremlin.data.edge.Relationship
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.edge.RelationshipRepository
import org.springframework.stereotype.Component
import java.net.URI
import java.time.OffsetDateTime

@Component
class RelationshipDataSupport(private val relationshipRepository: RelationshipRepository) : AbstractDataSupport() {

    fun randomRelationship(sourceEntity: BaseVertex, targetEntity: BaseVertex,
                           entityFunction: (Relationship) -> Relationship = { it }): Relationship {
        return relationshipRepository.save(
                entityFunction(
                        Relationship(
                                created = randomGenerator.nextObject(OffsetDateTime::class.java),
                                author = randomGenerator.nextObject(String::class.java),
                                source = sourceEntity.id,
                                target = targetEntity.id,
                                state = randomGenerator.nextObject(GraphEntityState::class.java),
                                type = randomGenerator.nextObject(RelationshipType::class.java),
                                informationSource = URI("http://google.com")
                        )
                )
        )
    }

    fun randomActiveRelationship(source: String, target: String,
                                 entityFunction: (Relationship) -> Relationship = { it }): Relationship {
        return relationshipRepository.save(
                entityFunction(
                        Relationship(
                                created = randomGenerator.nextObject(OffsetDateTime::class.java),
                                author = randomGenerator.nextObject(String::class.java),
                                source = source,
                                target = target,
                                state = graphEntityActiveStates.random(),
                                type = randomGenerator.nextObject(RelationshipType::class.java),
                                informationSource = URI("http://google.com")
                        )
                )
        )
    }

    fun randomInactiveRelationship(source: String, target: String,
                                   entityFunction: (Relationship) -> Relationship = { it }): Relationship {
        return relationshipRepository.save(
                entityFunction(
                        Relationship(
                                created = randomGenerator.nextObject(OffsetDateTime::class.java),
                                author = randomGenerator.nextObject(String::class.java),
                                source = source,
                                target = target,
                                state = graphEntityInactiveStates.random(),
                                type = randomGenerator.nextObject(RelationshipType::class.java),
                                informationSource = URI("http://google.com")
                        )
                )
        )
    }

    fun randomCreateRelationship(source: String, target: String): CreateRelationshipTO {
        return CreateRelationshipTO(
                source = source,
                target = target,
                type = randomGenerator.nextObject(RelationshipType::class.java),
                informationSource = URI("http://google.com"),
                description = randomGenerator.nextObject(String::class.java)
        )
    }
}
