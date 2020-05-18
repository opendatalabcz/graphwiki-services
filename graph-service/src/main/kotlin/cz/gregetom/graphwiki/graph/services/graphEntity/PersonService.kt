package cz.gregetom.graphwiki.graph.services.graphEntity

import cz.gregetom.graphwiki.api.graph.model.CreatePersonTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Person
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex.PersonRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class PersonService(private val personRepository: PersonRepository)
    : AbstractGraphEntityService<Person>(GraphEntityType.PERSON, personRepository) {

    /**
     * Create new person and entity request.
     *
     * @param createPersonTO new person value
     * @return id of related entity request
     */
    fun create(createPersonTO: CreatePersonTO): String {
        LOGGER.info("Create new person $createPersonTO")
        return personRepository.save(
                Person(
                        author = UserAccessor.currentUserIdOrThrow,
                        created = OffsetDateTime.now(),
                        state = GraphEntityState.CONCEPT,
                        givenName = createPersonTO.givenName,
                        familyName = createPersonTO.familyName,
                        gender = createPersonTO.gender,
                        nationality = createPersonTO.nationality,
                        dateOfBirth = createPersonTO.dateOfBirth,
                        occupation = createPersonTO.occupation,
                        informationSource = createPersonTO.informationSource
                )
        ).let { createRelatedEntityRequest(it) }
    }

    /**
     * Update person.
     *
     * @param id id of person to be updated
     * @param createPersonTO new person value
     */
    fun update(id: String, createPersonTO: CreatePersonTO) {
        LOGGER.info("Update person $id with $createPersonTO")
        personRepository.update(
                personRepository.findById(id).copy(
                        gender = createPersonTO.gender,
                        givenName = createPersonTO.givenName,
                        familyName = createPersonTO.familyName,
                        dateOfBirth = createPersonTO.dateOfBirth,
                        nationality = createPersonTO.nationality,
                        occupation = createPersonTO.occupation
                )
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PersonService::class.java)
    }
}
