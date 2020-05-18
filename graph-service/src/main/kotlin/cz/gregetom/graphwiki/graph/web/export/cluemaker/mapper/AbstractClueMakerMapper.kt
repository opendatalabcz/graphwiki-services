package cz.gregetom.graphwiki.graph.web.export.cluemaker.mapper

import org.apache.tinkerpop.gremlin.structure.Element

abstract class AbstractClueMakerMapper {

    /**
     * Get properties of [element] as map.
     *
     * @param element element
     * @return map of properties
     */
    fun getPropertyMap(element: Element): Map<String, String> {
        return element.properties<String>().asSequence().map { it.key() to it.value() }.toMap()
    }
}
