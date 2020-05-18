package cz.gregetom.graphwiki.graph.web.graphEntity.company

import cz.gregetom.graphwiki.api.graph.model.AddressTO
import cz.gregetom.graphwiki.api.graph.model.CompanyTO
import cz.gregetom.graphwiki.api.graph.model.CompanyTOLinks
import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Company
import cz.gregetom.graphwiki.graph.web.*
import cz.gregetom.graphwiki.graph.web.graphEntity.AbstractGraphEntityMapper
import org.springframework.stereotype.Component

@Component
class CompanyMapper(private val linkFactory: LinkFactory) : AbstractGraphEntityMapper() {

    fun map(company: Company): CompanyTO {
        return CompanyTO(
                id = company.id,
                state = company.state,
                author = linkFactory.userSelf(company.author, "author").toLinkTO(),
                created = company.created,
                officialName = company.officialName,
                registrationNumber = company.registrationNumber,
                headquarters = AddressTO(
                        street = company.headquarters.street,
                        houseNumber = company.headquarters.houseNumber,
                        postalCode = company.headquarters.postalCode,
                        landRegistryNumber = company.headquarters.landRegistryNumber,
                        city = company.headquarters.city,
                        country = company.headquarters.country
                ),
                industry = company.industry,
                inception = company.inception,
                informationSource = company.informationSource,
                informationSourceHost = company.informationSource.host,
                links = mapLinks(company)
        )
    }

    private fun mapLinks(company: Company): CompanyTOLinks {
        return CompanyTOLinks(
                self = CompanyApiLinks.self(company.id).toLinkTO(),
                update = company.ifUpdateAvailable { CompanyApiLinks.update(company.id).toLinkTO() },
                delete = company.ifDeleteAvailable { CompanyApiLinks.delete(company.id).toLinkTO() },
                restore = company.ifRestoreAvailable { CompanyApiLinks.restore(company.id).toLinkTO() },
                relationships = RelationshipApiLinks.findRelatedForVertex(company.id).toLinkTO(),
                graph = GraphApiLinks.getGraph(company.id).toLinkTO(),
                complaintCreate = company.ifComplaintAvailable { ComplaintApiLinks.setup(company.id, GraphEntityType.COMPANY).toLinkTO() },
                complaintList = ComplaintApiLinks.entityRelated(company.id).toLinkTO(),
                history = HistoryApiLinks.forEntity(company.id).toLinkTO()
        )
    }
}
