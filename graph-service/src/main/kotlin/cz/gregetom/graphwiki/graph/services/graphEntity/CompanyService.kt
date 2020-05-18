package cz.gregetom.graphwiki.graph.services.graphEntity

import cz.gregetom.graphwiki.api.graph.model.CreateCompanyTO
import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Address
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Company
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex.CompanyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class CompanyService(private val companyRepository: CompanyRepository)
    : AbstractGraphEntityService<Company>(GraphEntityType.COMPANY, companyRepository) {

    /**
     * Create new company and entity request.
     *
     * @param createCompanyTO new company value
     * @return id of related entity request
     */
    fun create(createCompanyTO: CreateCompanyTO): String {
        LOGGER.info("Create new company $createCompanyTO")
        return companyRepository.save(
                Company(
                        author = UserAccessor.currentUserIdOrThrow,
                        created = OffsetDateTime.now(),
                        officialName = createCompanyTO.officialName,
                        registrationNumber = createCompanyTO.registrationNumber,
                        headquarters = Address(
                                author = UserAccessor.currentUserIdOrThrow,
                                created = OffsetDateTime.now(),
                                street = createCompanyTO.headquarters.street,
                                houseNumber = createCompanyTO.headquarters.houseNumber,
                                landRegistryNumber = createCompanyTO.headquarters.landRegistryNumber,
                                postalCode = createCompanyTO.headquarters.postalCode,
                                city = createCompanyTO.headquarters.city,
                                country = createCompanyTO.headquarters.country
                        ),
                        industry = createCompanyTO.industry,
                        inception = createCompanyTO.inception,
                        state = GraphEntityState.CONCEPT,
                        informationSource = createCompanyTO.informationSource
                )
        ).let { createRelatedEntityRequest(it) }
    }

    /**
     * Update company.
     *
     * @param id id of company to be updated
     * @param createCompanyTO new company value
     */
    fun update(id: String, createCompanyTO: CreateCompanyTO) {
        LOGGER.info("Update company $id with $createCompanyTO")
        val company = companyRepository.findById(id)
        companyRepository.update(
                company.copy(
                        officialName = createCompanyTO.officialName,
                        registrationNumber = createCompanyTO.registrationNumber,
                        industry = createCompanyTO.industry,
                        informationSource = createCompanyTO.informationSource,
                        inception = createCompanyTO.inception,
                        headquarters = company.headquarters.copy(
                                street = createCompanyTO.headquarters.street,
                                houseNumber = createCompanyTO.headquarters.houseNumber,
                                postalCode = createCompanyTO.headquarters.postalCode,
                                city = createCompanyTO.headquarters.city,
                                country = createCompanyTO.headquarters.country,
                                landRegistryNumber = createCompanyTO.headquarters.landRegistryNumber
                        )
                )
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CompanyService::class.java)
    }
}
