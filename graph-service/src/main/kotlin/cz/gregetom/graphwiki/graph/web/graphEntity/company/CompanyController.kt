package cz.gregetom.graphwiki.graph.web.graphEntity.company

import cz.gregetom.graphwiki.api.graph.api.CompanyApi
import cz.gregetom.graphwiki.api.graph.model.CompanyTO
import cz.gregetom.graphwiki.api.graph.model.CreateCompanyTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.graph.services.graphEntity.CompanyService
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
class CompanyController(
        private val companyService: CompanyService,
        private val companyMapper: CompanyMapper
) : CompanyApi {

    @Transactional(readOnly = true)
    override fun findCompanyById(@Size(max = 50) @PathVariable id: String): ResponseEntity<CompanyTO> {
        return ResponseEntity.ok(companyMapper.map(companyService.findById(id)))
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun updateCompany(@Size(max = 50) @PathVariable id: String,
                               @Valid @RequestBody createCompanyTO: CreateCompanyTO): ResponseEntity<Unit> {
        return ResponseEntity.ok(companyService.update(id, createCompanyTO))
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun companyStateTransition(@Size(max = 50) @PathVariable id: String,
                                        @NotNull @RequestParam nextState: GraphEntityState): ResponseEntity<Unit> {
        companyService.moveToState(id, nextState, true)
        return ResponseEntity.ok().build()
    }

    @Secured(Roles.ROLE_ADMIN)
    @Transactional
    override fun deleteCompany(@Size(max = 50) @PathVariable id: String): ResponseEntity<Unit> {
        return ResponseEntity.ok(companyService.delete(id))
    }

    @Secured(Roles.ROLE_USER, Roles.ROLE_ADMIN)
    @Transactional
    override fun createCompany(@Valid @RequestBody createCompanyTO: CreateCompanyTO): ResponseEntity<Unit> {
        val entityRequestId = companyService.create(createCompanyTO)
        return ResponseEntity
                .created(EntityRequestApiLinks.self(entityRequestId).toUri())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION)
                .build()
    }
}
