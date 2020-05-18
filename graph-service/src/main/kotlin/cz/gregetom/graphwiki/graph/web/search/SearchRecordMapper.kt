package cz.gregetom.graphwiki.graph.web.search

import cz.gregetom.graphwiki.api.graph.model.AddressTO
import cz.gregetom.graphwiki.api.graph.model.SearchCompanyRecord
import cz.gregetom.graphwiki.api.graph.model.SearchPersonRecord
import cz.gregetom.graphwiki.api.graph.model.SearchPersonRecordLinks
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Company
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Person
import cz.gregetom.graphwiki.graph.web.CompanyApiLinks
import cz.gregetom.graphwiki.graph.web.GraphApiLinks
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import cz.gregetom.graphwiki.graph.web.toLinkTO
import org.springframework.stereotype.Component

@Component
class SearchRecordMapper {

    fun map(person: Person): SearchPersonRecord {
        return SearchPersonRecord(
                id = person.id,
                givenName = person.givenName,
                familyName = person.familyName,
                dateOfBirth = person.dateOfBirth,
                links = SearchPersonRecordLinks(
                        self = PersonApiLinks.self(person.id).toLinkTO(),
                        graph = GraphApiLinks.getGraph(person.id).toLinkTO(),
                        vertex = GraphApiLinks.findVertexById(person.id).toLinkTO()
                )
        )
    }

    fun map(company: Company): SearchCompanyRecord {
        return SearchCompanyRecord(
                id = company.id,
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
                links = SearchPersonRecordLinks(
                        self = CompanyApiLinks.self(company.id).toLinkTO(),
                        graph = GraphApiLinks.getGraph(company.id).toLinkTO(),
                        vertex = GraphApiLinks.findVertexById(company.id).toLinkTO()
                )
        )
    }
}
