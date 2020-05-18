package cz.gregetom.graphwiki.graph.web.graph

import cz.gregetom.graphwiki.api.graph.model.LinkTO
import cz.gregetom.graphwiki.api.graph.model.VertexTO
import cz.gregetom.graphwiki.api.graph.model.VertexTOLinks
import cz.gregetom.graphwiki.api.graph.model.VertexType
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.BaseVertex
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexVisitor
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Company
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Person
import cz.gregetom.graphwiki.graph.web.CompanyApiLinks
import cz.gregetom.graphwiki.graph.web.GraphApiLinks
import cz.gregetom.graphwiki.graph.web.PersonApiLinks
import cz.gregetom.graphwiki.graph.web.toLinkTO

class VertexMapperVisitor(private val searchedVertex: BaseVertex? = null) : VertexVisitor<VertexTO> {

    override fun visit(person: Person): VertexTO {
        return VertexTO(
                id = person.id,
                label = "${person.givenName} ${person.familyName}",
                type = VertexType.PERSON,
                links = VertexTOLinks(
                        self = PersonApiLinks.self(person.id).toLinkTO(),
                        graph = person.ifGraphSearchAvailable(searchedVertex) { GraphApiLinks.getGraph(person.id).toLinkTO() }
                )
        )
    }

    override fun visit(company: Company): VertexTO {
        return VertexTO(
                id = company.id,
                label = company.officialName,
                type = VertexType.COMPANY,
                links = VertexTOLinks(
                        self = CompanyApiLinks.self(company.id).toLinkTO(),
                        graph = company.ifGraphSearchAvailable(searchedVertex) { GraphApiLinks.getGraph(company.id).toLinkTO() }
                )
        )
    }
}

private fun BaseVertex.ifGraphSearchAvailable(searchedVertex: BaseVertex?, block: () -> LinkTO?): LinkTO? {
    return if (this.id != searchedVertex?.id) block() else null
}
