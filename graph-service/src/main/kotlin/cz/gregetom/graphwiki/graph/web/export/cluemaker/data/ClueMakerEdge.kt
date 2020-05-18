package cz.gregetom.graphwiki.graph.web.export.cluemaker.data

data class ClueMakerEdge(
        val id: Long,
        val label: String,
        val entity: String,
        val sourceId: Long,
        val targetId: Long,
        val attributes: Map<String, String> = emptyMap(),
        val directed: Boolean = true,
        val weight: Double = 1.0,
        val labelPosition: Double = 0.5,
        val thickness: Double = 1.0,
        val labelFontSize: Double = 1.0,
        val style: String = "NORMAL"
)
