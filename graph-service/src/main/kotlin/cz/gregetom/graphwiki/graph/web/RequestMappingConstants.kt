package cz.gregetom.graphwiki.graph.web

object RequestMappingConstants {
    const val FULLTEXT_SEARCH = "/search"

    const val GRAPH = "/graph"
    const val GRAPH_VERTEX_FIND = "/graph/vertex/*"
    const val GRAPH_EXPORT_GRAPHML = "/graph/export-graphml"
    const val GRAPH_EXPORT_CLUEMAKER = "/graph/export-cluemaker"

    const val PERSON_CREATE = "/graph-entity/person"
    const val PERSON_FIND = "/graph-entity/person/*"
    const val PERSON_UPDATE = "/graph-entity/person/*"
    const val PERSON_DELETE = "/graph-entity/person/*"
    const val PERSON_STATE_TRANSITION = "/graph-entity/person/*/state"

    const val COMPANY_CREATE = "/graph-entity/company"
    const val COMPANY_FIND = "/graph-entity/company/*"
    const val COMPANY_UPDATE = "/graph-entity/company/*"
    const val COMPANY_DELETE = "/graph-entity/company/*"
    const val COMPANY_STATE_TRANSITION = "/graph-entity/company/*/state"

    const val RELATIONSHIP_CREATE = "/graph-entity/relationship"
    const val RELATIONSHIP_FIND = "/graph-entity/relationship/*"
    const val RELATIONSHIP_UPDATE = "/graph-entity/relationship/*"
    const val RELATIONSHIP_DELETE = "/graph-entity/relationship/*"
    const val RELATIONSHIP_STATE_TRANSITION = "/graph-entity/relationship/*/state"
    const val RELATIONSHIP_RELATED_FOR_VERTEX = "/graph-entity/relationships"

    const val ENTITY_REQUEST_FIND = "/entity-request/*"
    const val ENTITY_REQUEST_STATE_TRANSITION = "/entity-request/*/state"
    const val ENTITY_REQUEST_ASSIGNMENT = "/entity-request/*/assignment"

    const val COMPLAINT_SETUP = "/complaint"
    const val COMPLAINT_CREATE = "/complaint"
    const val COMPLAINT_FIND = "/complaint/*"
    const val COMPLAINT_STATE_TRANSITION = "/complaint/*/state"
    const val COMPLAINT_RELATED = "/complaint/*/related"
    const val COMPLAINT_ASSIGNMENT = "/complaint/*/assignment"
    const val COMPLAINT_RELATED_FOR_GRAPH_ENTITY = "/complaints"

    const val HISTORY = "/history"
}
