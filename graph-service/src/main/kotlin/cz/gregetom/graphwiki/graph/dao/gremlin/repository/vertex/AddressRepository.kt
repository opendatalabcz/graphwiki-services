package cz.gregetom.graphwiki.graph.dao.gremlin.repository.vertex

import cz.gregetom.graphwiki.graph.dao.framework.repository.vertex.AbstractVertexRepository
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Address

class AddressRepository : AbstractVertexRepository<Address>(Address::class)
