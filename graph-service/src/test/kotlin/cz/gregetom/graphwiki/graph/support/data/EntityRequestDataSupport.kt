package cz.gregetom.graphwiki.graph.support.data

import cz.gregetom.graphwiki.api.graph.model.EntityRequestState
import cz.gregetom.graphwiki.graph.dao.jpa.data.EntityRequest
import cz.gregetom.graphwiki.graph.dao.jpa.repository.EntityRequestRepository
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.net.URI

@Component
class EntityRequestDataSupport(private val entityRequestRepository: EntityRequestRepository) : AbstractDataSupport() {

    fun randomCurrentEntityRequest(graphEntityId: String, entityFunction: (EntityRequest) -> EntityRequest = { it }): EntityRequest {
        return entityRequestRepository.save(
                entityFunction(randomGenerator.nextObject(EntityRequest::class.java)).copy(
                        state = randomCurrentState(),
                        entityId = graphEntityId,
                        ipAddress = InetAddress.getByName("google.com"),
                        taskUrl = URI("http://test-task.cz")
                )
        )
    }

    fun randomHistoricEntityRequest(graphEntityId: String, entityFunction: (EntityRequest) -> EntityRequest = { it }): EntityRequest {
        return entityRequestRepository.save(
                entityFunction(randomGenerator.nextObject(EntityRequest::class.java)).copy(
                        state = randomHistoricState(),
                        entityId = graphEntityId,
                        ipAddress = InetAddress.getByName("google.com"),
                        taskUrl = URI("http://test-task.cz")
                )
        )
    }

    fun randomCurrentState(): EntityRequestState {
        return listOf(EntityRequestState.NEW).random()
    }

    fun randomHistoricState(): EntityRequestState {
        return listOf(EntityRequestState.APPROVED, EntityRequestState.REJECTED, EntityRequestState.ENTITY_MODIFIED).random()
    }
}
