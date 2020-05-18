package cz.gregetom.graphwiki.graph.dao.framework.data.vertex

import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Address
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Company
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Person

interface VertexVisitor<T> {

    fun visit(person: Person): T {
        throw IllegalStateException("not supported")
    }

    fun visit(company: Company): T {
        throw IllegalStateException("not supported")
    }

    fun visit(address: Address): T {
        throw IllegalStateException("not supported")
    }
}
