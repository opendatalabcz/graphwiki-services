package cz.gregetom.graphwiki.graph.support.data

import cz.gregetom.graphwiki.api.graph.model.ComplaintState
import cz.gregetom.graphwiki.api.graph.model.CreateComplaintTO
import cz.gregetom.graphwiki.graph.dao.jpa.data.Complaint
import cz.gregetom.graphwiki.graph.dao.jpa.repository.ComplaintRepository
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.net.URI

@Component
class ComplaintDataSupport(private val complaintRepository: ComplaintRepository) : AbstractDataSupport() {

    fun randomCurrentComplaint(graphEntityId: String, entityFunction: (Complaint) -> Complaint = { it }): Complaint {
        return complaintRepository.save(
                entityFunction(randomGenerator.nextObject(Complaint::class.java)).copy(
                        state = randomCurrentState(),
                        entityId = graphEntityId,
                        ipAddress = InetAddress.getByName("google.com"),
                        taskUrl = URI("http://test-task.cz")
                )
        )
    }

    fun randomHistoricComplaint(graphEntityId: String, entityFunction: (Complaint) -> Complaint = { it }): Complaint {
        return complaintRepository.save(
                entityFunction(randomGenerator.nextObject(Complaint::class.java)).copy(
                        state = randomHistoricState(),
                        entityId = graphEntityId,
                        ipAddress = InetAddress.getByName("google.com"),
                        taskUrl = URI("http://test-task.cz")
                )
        )
    }

    fun randomCreateComplaintTO(): CreateComplaintTO {
        return randomGenerator.nextObject(CreateComplaintTO::class.java)
    }

    fun randomCurrentState(): ComplaintState {
        return listOf(ComplaintState.ADMIN_DECISION).random()
    }

    fun randomHistoricState(): ComplaintState {
        return listOf(ComplaintState.APPROVED, ComplaintState.REJECTED, ComplaintState.ENTITY_MODIFIED).random()
    }
}
