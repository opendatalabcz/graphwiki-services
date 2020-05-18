package cz.gregetom.graphwiki.graph.support.data

import cz.gregetom.graphwiki.api.graph.model.CreatePersonTO
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Person
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex.PersonRepository
import org.springframework.stereotype.Component
import java.net.URI

@Component
class PersonDataSupport(private val personRepository: PersonRepository) : AbstractDataSupport() {

    fun randomPerson(entityFunction: (Person) -> Person = { it }): Person {
        return personRepository.save(entityFunction(randomGenerator.nextObject(Person::class.java).copy(informationSource = URI("http://google.com"))))
    }

    fun randomCreatePersonTO(): CreatePersonTO {
        return randomGenerator.nextObject(CreatePersonTO::class.java).copy(informationSource = URI("http://google.com"))
    }

    fun randomActivePerson(entityFunction: (Person) -> Person = { it }): Person {
        return personRepository.save(
                entityFunction(
                        randomGenerator.nextObject(Person::class.java)
                                .copy(state = graphEntityActiveStates.random())
                                .copy(informationSource = URI("http://google.com"))
                )
        )
    }

    fun randomInactivePerson(entityFunction: (Person) -> Person = { it }): Person {
        return personRepository.save(
                entityFunction(
                        randomGenerator.nextObject(Person::class.java)
                                .copy(state = graphEntityInactiveStates.random())
                                .copy(informationSource = URI("http://google.com"))
                )
        )
    }
}
