package cz.gregetom.graphwiki.graph.support.data

import cz.gregetom.graphwiki.api.graph.model.CreateCompanyTO
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Company
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex.CompanyRepository
import org.springframework.stereotype.Component
import java.net.URI

@Component
class CompanyDataSupport(private val companyRepository: CompanyRepository) : AbstractDataSupport() {

    fun randomCompany(entityFunction: (Company) -> Company = { it }): Company {
        return companyRepository.save(entityFunction(randomGenerator.nextObject(Company::class.java).copy(informationSource = URI("http://google.com"))))
    }

    fun randomCreateCompanyTO(): CreateCompanyTO {
        return randomGenerator.nextObject(CreateCompanyTO::class.java).copy(informationSource = URI("http://google.com"))
    }

    fun randomActiveCompany(entityFunction: (Company) -> Company = { it }): Company {
        return companyRepository.save(
                entityFunction(
                        randomGenerator.nextObject(Company::class.java)
                                .copy(state = graphEntityActiveStates.random())
                                .copy(informationSource = URI("http://google.com"))
                )
        )
    }

    fun randomInactiveCompany(entityFunction: (Company) -> Company = { it }): Company {
        return companyRepository.save(
                entityFunction(
                        randomGenerator.nextObject(Company::class.java)
                                .copy(state = graphEntityInactiveStates.random())
                                .copy(informationSource = URI("http://google.com"))
                )
        )
    }
}
