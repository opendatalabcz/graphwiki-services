package cz.gregetom.graphwiki.user

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.jdbc.datasource.SingleConnectionDataSource
import java.sql.DriverManager
import java.util.*
import javax.sql.DataSource

@TestConfiguration
@ComponentScan(basePackages = ["cz.gregetom.graphwiki.user.support.data"])
class TestConfig {

    @Bean
    fun embeddedPostgresDataSource(): DataSource {
        val embeddedPostgres = EmbeddedPostgres.builder().start()
        val connection = DriverManager.getConnection(
                embeddedPostgres.getJdbcUrl("postgres", "postgres"),
                Properties().apply { this.setProperty("stringtype", "unspecified") }
        )
        return SingleConnectionDataSource(connection, true)
    }
}
