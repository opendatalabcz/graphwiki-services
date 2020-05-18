package cz.gregetom.graphwiki.graph.web.graphEntity.person

import cz.gregetom.graphwiki.api.graph.api.PersonApi
import cz.gregetom.graphwiki.api.graph.model.CreatePersonTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.PersonTO
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.graph.services.graphEntity.PersonService
import cz.gregetom.graphwiki.graph.web.EntityRequestApiLinks
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@RestController
@RequestMapping
@Validated
class PersonController(
        private val personService: PersonService,
        private val personMapper: PersonMapper
) : PersonApi {

    @Transactional(readOnly = true)
    override fun findPersonById(@Size(max = 50) @PathVariable id: String): ResponseEntity<PersonTO> {
        return ResponseEntity.ok(personMapper.map(personService.findById(id)))
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun updatePerson(@Size(max = 50) @PathVariable id: String,
                              @Valid @RequestBody createPersonTO: CreatePersonTO): ResponseEntity<Unit> {
        return ResponseEntity.ok(personService.update(id, createPersonTO))
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun deletePerson(@Size(max = 50) @PathVariable id: String): ResponseEntity<Unit> {
        return ResponseEntity.ok(personService.delete(id))
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun personStateTransition(@Size(max = 50) @PathVariable id: String,
                                       @NotNull @RequestParam nextState: GraphEntityState): ResponseEntity<Unit> {
        personService.moveToState(id, nextState, true)
        return ResponseEntity.ok().build()
    }

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional
    override fun createPerson(@Valid @RequestBody createPersonTO: CreatePersonTO): ResponseEntity<Unit> {
        val entityRequestId = personService.create(createPersonTO)
        return ResponseEntity
                .created(EntityRequestApiLinks.self(entityRequestId).toUri())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION)
                .build()
    }
}
