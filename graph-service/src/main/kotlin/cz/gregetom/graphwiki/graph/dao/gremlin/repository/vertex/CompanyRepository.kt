package cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex

import cz.gregetom.graphwiki.graph.dao.framework.repository.vertex.AbstractVertexRepository
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Company
import cz.gregetom.graphwiki.graph.dao.gremlin.repository.GraphEntityRepository
import org.springframework.stereotype.Component

@Component
class CompanyRepository : AbstractVertexRepository<Company>(Company::class), GraphEntityRepository<Company>
