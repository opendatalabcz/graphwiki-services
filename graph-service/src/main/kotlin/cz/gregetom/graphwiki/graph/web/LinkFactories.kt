package cz.gregetom.graphwiki.graph.web

import cz.gregetom.graphwiki.api.graph.model.GraphEntityType
import cz.gregetom.graphwiki.api.graph.model.LinkTO
import cz.gregetom.graphwiki.api.java.comment.api.CommentApi
import cz.gregetom.graphwiki.api.java.graph.api.*
import cz.gregetom.graphwiki.api.java.graph.model.ComplaintState
import cz.gregetom.graphwiki.api.java.graph.model.EntityRequestState
import cz.gregetom.graphwiki.api.java.graph.model.GraphEntityState
import cz.gregetom.graphwiki.api.java.task.api.TaskApi
import cz.gregetom.graphwiki.api.java.user.api.UserApi
import cz.gregetom.graphwiki.commons.web.LinkBuilder
import cz.gregetom.graphwiki.graph.web.graphEntity.company.CompanyController
import cz.gregetom.graphwiki.graph.web.relatedEntity.complaint.ComplaintController
import org.springframework.beans.factory.annotation.Value
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component
import java.net.URI

object GraphEntityApiLinks {
    fun selfByType(id: String, type: GraphEntityType): Link {
        return when (type) {
            GraphEntityType.PERSON -> PersonApiLinks.self(id)
            GraphEntityType.COMPANY -> CompanyApiLinks.self(id)
            GraphEntityType.RELATIONSHIP -> RelationshipApiLinks.self(id)
        }
    }
}

object PersonApiLinks {
    fun self(id: String): Link {
        return linkTo(methodOn(PersonApi::class.java).findPersonById(id)).withSelfRel()
    }

    fun create(): Link {
        return linkTo(methodOn(PersonApi::class.java).createPerson(null)).withRel("create")
    }

    fun delete(id: String): Link {
        return linkTo(methodOn(PersonApi::class.java).deletePerson(id)).withRel("delete")
    }

    fun restore(id: String): Link {
        return linkTo(methodOn(PersonApi::class.java).personStateTransition(id, GraphEntityState.ACTIVE)).withRel("restore")
    }

    fun update(id: String): Link {
        return linkTo(methodOn(PersonApi::class.java).updatePerson(id, null)).withRel("update")
    }
}

object CompanyApiLinks {
    fun self(id: String): Link {
        return linkTo(methodOn(CompanyController::class.java).findCompanyById(id)).withSelfRel()
    }

    fun create(): Link {
        return linkTo(methodOn(CompanyApi::class.java).createCompany(null)).withRel("create")
    }

    fun delete(id: String): Link {
        return linkTo(methodOn(CompanyApi::class.java).deleteCompany(id)).withRel("delete")
    }

    fun restore(id: String): Link {
        return linkTo(methodOn(CompanyApi::class.java).companyStateTransition(id, GraphEntityState.ACTIVE)).withRel("restore")
    }

    fun update(id: String): Link {
        return linkTo(methodOn(CompanyApi::class.java).updateCompany(id, null)).withRel("update")
    }
}

object RelationshipApiLinks {
    fun self(id: String): Link {
        return linkTo(methodOn(RelationshipApi::class.java).findRelationshipById(id)).withSelfRel()
    }

    fun create(): Link {
        return linkTo(methodOn(RelationshipApi::class.java).createRelationship(null)).withRel("create")
    }

    fun findRelatedForVertex(vertexId: String): Link {
        return linkTo(methodOn(RelationshipApi::class.java).findRelatedRelationshipsForVertex(vertexId)).withRel("relationships")
    }

    fun delete(id: String): Link {
        return linkTo(methodOn(RelationshipApi::class.java).deleteRelationship(id)).withRel("delete")
    }

    fun restore(id: String): Link {
        return linkTo(methodOn(RelationshipApi::class.java).relationshipStateTransition(id, GraphEntityState.ACTIVE)).withRel("restore")
    }

    fun update(id: String): Link {
        return linkTo(methodOn(RelationshipApi::class.java).updateRelationship(id, null)).withRel("update")
    }
}

object ComplaintApiLinks {
    fun self(id: String): Link {
        return linkTo(methodOn(ComplaintController::class.java).findComplaintById(id)).withSelfRel()
    }

    fun assign(id: String, assignee: String? = null): Link {
        return if (assignee !== null) {
            linkTo(methodOn(ComplaintApi::class.java).assignComplaint(id, assignee)).withRel("assign")
        } else {
            Link(
                    linkTo(methodOn(ComplaintController::class.java).assignComplaint(id, ""))
                            .toUriComponentsBuilder()
                            .replaceQuery(null)
                            .build()
                            .toUriString(), "assign")
        }
    }

    fun setup(id: String, entityType: GraphEntityType): Link {
        return linkTo(methodOn(ComplaintApi::class.java).complaintSetup(id, cz.gregetom.graphwiki.api.java.graph.model.GraphEntityType.valueOf(entityType.value))).withRel("complaintSetup")
    }

    fun create(entityId: String, entityType: GraphEntityType): Link {
        return linkTo(methodOn(ComplaintApi::class.java).createComplaint(entityId, cz.gregetom.graphwiki.api.java.graph.model.GraphEntityType.valueOf(entityType.value), null)).withRel("create")
    }

    fun entityRelated(entityId: String): Link {
        return linkTo(methodOn(ComplaintApi::class.java).findRelatedComplaintsByEntityId(entityId)).withRel("related")
    }

    fun complaintRelated(complaintId: String): Link {
        return linkTo(methodOn(ComplaintApi::class.java).findRelatedComplaintsByComplaintId(complaintId)).withRel("related")
    }

    fun approve(id: String): Link {
        return linkTo(methodOn(ComplaintApi::class.java).complaintStateTransition(id, ComplaintState.APPROVED)).withRel("approve")
    }

    fun reject(id: String): Link {
        return linkTo(methodOn(ComplaintApi::class.java).complaintStateTransition(id, ComplaintState.REJECTED)).withRel("reject")
    }
}

object EntityRequestApiLinks {
    fun self(id: String): Link {
        return linkTo(methodOn(EntityRequestApi::class.java).findEntityRequestById(id)).withSelfRel()
    }

    fun assign(id: String, assignee: String? = null): Link {
        return if (assignee !== null) {
            linkTo(methodOn(EntityRequestApi::class.java).assignEntityRequest(id, assignee)).withRel("assign")
        } else {
            return Link(
                    linkTo(methodOn(EntityRequestApi::class.java).assignEntityRequest(id, ""))
                            .toUriComponentsBuilder()
                            .replaceQuery(null)
                            .build()
                            .toUriString(), "assign")
        }
    }

    fun approve(id: String): Link {
        return linkTo(methodOn(EntityRequestApi::class.java).entityRequestStateTransition(id, EntityRequestState.APPROVED)).withRel("approve")
    }

    fun reject(id: String): Link {
        return linkTo(methodOn(EntityRequestApi::class.java).entityRequestStateTransition(id, EntityRequestState.REJECTED)).withRel("reject")
    }
}

object HistoryApiLinks {
    fun forEntity(entityId: String): Link {
        return linkTo(methodOn(HistoryApi::class.java).findAllByEntityId(entityId)).withRel("history")
    }
}

object SearchApiLinks {
    fun fulltextSearch(query: String, page: Int): Link {
        return linkTo(methodOn(SearchApi::class.java).fulltextSearch(query, page)).withRel("search")
    }

    fun fulltextSearchWithoutQueryParams(): Link {
        return Link(
                linkTo(methodOn(SearchApi::class.java).fulltextSearch("", 1))
                        .toUriComponentsBuilder()
                        .replaceQuery(null)
                        .build()
                        .toUriString(),
                "search"
        )
    }
}

object GraphApiLinks {
    fun getGraph(vertexId: String): Link {
        return linkTo(methodOn(GraphApi::class.java).getGraph(vertexId)).withSelfRel()
    }

    fun findVertexById(id: String): Link {
        return linkTo(methodOn(GraphApi::class.java).findVertexById(id)).withSelfRel()
    }
}

object ExportApiLinks {
    fun graphML(id: String): Link {
        return linkTo(methodOn(ExportApi::class.java).exportGraphML(id)).withRel("exportGraphML")
    }

    fun clueMaker(id: String): Link {
        return linkTo(methodOn(ExportApi::class.java).exportClueMaker(id)).withRel("exportClueMaker")
    }
}

@Component
class LinkFactory(
        @Value("\${graphwiki.web.services.user.base-url}")
        private val userServiceBaseUrl: URI,
        @Value("\${graphwiki.web.services.comment.base-url}")
        private val commentServiceBaseUrl: URI,
        @Value("\${graphwiki.web.services.task.base-url}")
        private val taskServiceBaseUrl: URI
) {

    fun userSelf(userId: String, rel: String): Link {
        return LinkBuilder.anotherServiceLink(userServiceBaseUrl, methodOn(UserApi::class.java).findById(userId), rel)
    }

    fun commentList(entityId: String): Link {
        return LinkBuilder.anotherServiceLink(commentServiceBaseUrl, methodOn(CommentApi::class.java).findAllByEntityId(entityId), "commentList")
    }

    fun commentCreate(entityId: String): Link {
        return LinkBuilder.anotherServiceLink(commentServiceBaseUrl, methodOn(CommentApi::class.java).create(entityId, null), "commentCreate")
    }

    fun taskCreate(): Link {
        return LinkBuilder.anotherServiceLink(taskServiceBaseUrl, methodOn(TaskApi::class.java).create(null), "taskCreate")
    }
}

fun Link.toLinkTO(): LinkTO {
    return LinkTO(
            href = this.href,
            rel = this.rel.value()
    )
}

fun URI.toLinkTO(rel: String): LinkTO {
    return LinkTO(
            href = this.toString(),
            rel = rel
    )
}
