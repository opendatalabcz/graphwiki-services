package cz.gregetom.graphwiki.graph.web.graphEntity.person

import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.api.graph.model.PersonTO
import cz.gregetom.graphwiki.api.graph.model.PersonTOLinks
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Person
import cz.gregetom.graphwiki.graph.web.*
import cz.gregetom.graphwiki.graph.web.graphEntity.AbstractGraphEntityMapper
import org.springframework.stereotype.Component

@Component
class PersonMapper(private val linkFactory: LinkFactory) : AbstractGraphEntityMapper() {

    fun map(person: Person): PersonTO {
        return PersonTO(
                id = person.id,
                author = linkFactory.userSelf(person.author, "author").toLinkTO(),
                created = person.created,
                state = person.state,
                familyName = person.familyName,
                givenName = person.givenName,
                gender = person.gender,
                nationality = person.nationality,
                dateOfBirth = person.dateOfBirth,
                occupation = person.occupation,
                informationSource = person.informationSource,
                informationSourceHost = person.informationSource.host,
                links = mapLinks(person)
        )
    }

    private fun mapLinks(person: Person): PersonTOLinks {
        return PersonTOLinks(
                self = PersonApiLinks.self(person.id).toLinkTO(),
                update = person.ifUpdateAvailable { PersonApiLinks.update(person.id).toLinkTO() },
                delete = person.ifDeleteAvailable { PersonApiLinks.delete(person.id).toLinkTO() },
                restore = person.ifRestoreAvailable { PersonApiLinks.restore(person.id).toLinkTO() },
                relationships = RelationshipApiLinks.findRelatedForVertex(person.id).toLinkTO(),
                graph = GraphApiLinks.getGraph(person.id).toLinkTO(),
                complaintCreate = person.ifComplaintAvailable { ComplaintApiLinks.setup(person.id, GraphEntityType.PERSON).toLinkTO() },
                complaintList = ComplaintApiLinks.entityRelated(person.id).toLinkTO(),
                history = HistoryApiLinks.forEntity(person.id).toLinkTO()
        )
    }
}
