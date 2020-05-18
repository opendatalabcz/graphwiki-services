package cz.gregetom.graphwiki.graph.web.export.cluemaker.data

data class ClueMakerNode(
        val id: Long,
        val label: String,
        val entity: String,
        val icon: String,
        val attributes: Map<String, String> = emptyMap(),
        val size: Double = 1.0,
        val secondaryLabels: List<String> = emptyList(),
        val fontSize: Double = 1.0,
        val alignment: String = "BOTTOM"
)
