package cz.gregetom.graphwiki.graph.web.export.cluemaker.mapper

import cz.gregetom.graphwiki.api.graph.model.Country
import cz.gregetom.graphwiki.graph.dao.framework.data.vertex.VertexType
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Address
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Company
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Person
import cz.gregetom.graphwiki.graph.web.export.cluemaker.data.ClueMakerNode
import org.apache.tinkerpop.gremlin.structure.Vertex

object ClueMakerVertexMapper : AbstractClueMakerMapper() {

    fun map(vertex: Vertex): ClueMakerNode {
        VertexType.valueOf(vertex.label()).let { type ->
            return when (type) {
                VertexType.PERSON -> ClueMakerPersonMapper.map(vertex, VertexType.PERSON)
                VertexType.COMPANY -> ClueMakerCompanyMapper.map(vertex, VertexType.COMPANY)
                VertexType.ADDRESS -> ClueMakerAddressMapper.map(vertex, VertexType.ADDRESS)
            }
        }
    }

    private object ClueMakerAddressMapper {
        fun map(vertex: Vertex, type: VertexType): ClueMakerNode {
            val propertyMap = getPropertyMap(vertex)
            return ClueMakerNode(
                    id = vertex.id().toString().toLong(),
                    label = Address.format(
                            street = propertyMap[Address::street.name] ?: error("property not available"),
                            postalCode = propertyMap[Address::postalCode.name] ?: error("property not available"),
                            landRegistryNumber = propertyMap[Address::landRegistryNumber.name],
                            houseNumber = propertyMap[Address::houseNumber.name] ?: error("property not available"),
                            city = propertyMap[Address::city.name] ?: error("property not available"),
                            country = Country.valueOf(propertyMap[Address::country.name]
                                    ?: error("property not available"))
                    ),
                    entity = resolveClueMakerVertexType(type),
                    attributes = propertyMap,
                    icon = resolveClueMakerIcon(type)
            )
        }
    }

    private object ClueMakerCompanyMapper {
        fun map(vertex: Vertex, type: VertexType): ClueMakerNode {
            val propertyMap = getPropertyMap(vertex)
            return ClueMakerNode(
                    id = vertex.id().toString().toLong(),
                    label = propertyMap[Company::officialName.name]!!,
                    entity = resolveClueMakerVertexType(type),
                    attributes = propertyMap,
                    icon = resolveClueMakerIcon(type)
            )
        }
    }

    private object ClueMakerPersonMapper {
        fun map(vertex: Vertex, type: VertexType): ClueMakerNode {
            val propertyMap = getPropertyMap(vertex)
            return ClueMakerNode(
                    id = vertex.id().toString().toLong(),
                    label = "${propertyMap[Person::givenName.name]} ${propertyMap[Person::familyName.name]}",
                    entity = resolveClueMakerVertexType(type),
                    attributes = propertyMap,
                    icon = resolveClueMakerIcon(type)
            )
        }
    }

    private fun resolveClueMakerVertexType(type: VertexType): String {
        return when (type) {
            VertexType.PERSON -> "Osoba"
            VertexType.COMPANY -> "Firma"
            VertexType.ADDRESS -> "Adresa"
        }
    }

    private fun resolveClueMakerIcon(type: VertexType): String {
        return when (type) {
            VertexType.PERSON -> "Osoba"
            VertexType.COMPANY -> "Firma "
            VertexType.ADDRESS -> "Adresa"
        }
    }
}
