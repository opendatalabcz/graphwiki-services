package cz.gregetom.graphwiki.graph.dao.gremlin.repository

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.commons.security.enums.Roles
import cz.gregetom.graphwiki.commons.security.util.UserAccessor
import cz.gregetom.graphwiki.graph.dao.gremlin.data.GraphEntity
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.reflect.KClass

private val DATA_MASTER_ROLE = arrayOf(Roles.ROLE_ADMIN)

fun GraphTraversalSource.findValidVertexById(id: String): GraphTraversal<Vertex, Vertex> {
    return this.V(id).onlyValidElement()
}

fun GraphTraversalSource.findValidEdgeById(id: String): GraphTraversal<Edge, Edge> {
    return this.E(id).onlyValidElement()
}

fun GraphTraversalSource.findAllValidVertices(): GraphTraversal<Vertex, Vertex> {
    return this.V().onlyValidElement()
}

fun <T : Element, U : Element> GraphTraversal<T, U>.onlyValidElement(): GraphTraversal<T, U> {
    if (UserAccessor.hasRoleAny(*DATA_MASTER_ROLE)) {
        return this
    } else {
        return this.has(GraphEntity::state.name, GraphEntityState.ACTIVE.name)
    }
}

fun <T, U> GraphTraversal<T, U>.nextOrThrow(kClass: KClass<*>, id: String): U {
    return this.tryNext().orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "${kClass.simpleName} with id $id not found!") }
}
