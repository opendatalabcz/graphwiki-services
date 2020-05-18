package cz.gregetom.graphwiki.graph.web.export.cluemaker

import org.apache.commons.io.FileUtils
import org.springframework.core.io.ClassPathResource
import java.io.BufferedOutputStream
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files

object ClueMakerArchiveUtil {

    /**
     * Copy template.spr from resources and replace data.
     *
     * @param dataByteContent exported graph data
     * @return archive with replaced data
     */
    fun getArchiveWithDataContent(dataByteContent: ByteArray): ByteArray {
        val file = Files.createTempFile("Graphwiki_export", ".spr")
        FileUtils.copyURLToFile(ClassPathResource(ARCHIVE_TEMPLATE_PATH).url, file.toFile())
        FileSystems.newFileSystem(file, null).use { fs ->
            BufferedOutputStream(Files.newOutputStream(fs.getPath(GRAPH_JSON_PATH))).use { it.write(dataByteContent) }
        }
        val content = File(file.toUri()).readBytes()
        Files.delete(file)
        return content
    }

    private const val ARCHIVE_TEMPLATE_PATH = "cluemaker/template.spr"
    private const val GRAPH_JSON_PATH = "/sheets/1/graph.json"
}
