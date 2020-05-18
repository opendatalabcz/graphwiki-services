package cz.gregetom.graphwiki.graph.web.export.cluemaker.data

data class ClueMakerExport(
        val nodes: List<ClueMakerNode>,
        val edges: List<ClueMakerEdge>
)
