package cz.gregetom.graphwiki.graph.support.data

import cz.gregetom.graphwiki.api.graph.model.GraphEntityState
import cz.gregetom.graphwiki.commons.test.RandomGenerator

open class AbstractDataSupport {

    protected val graphEntityActiveStates = setOf(GraphEntityState.ACTIVE)
    protected val graphEntityInactiveStates = GraphEntityState.values().toList().minus(graphEntityActiveStates).toSet()
    protected val randomGenerator = RandomGenerator.instance
}
