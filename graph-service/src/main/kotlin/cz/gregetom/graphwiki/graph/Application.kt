package cz.gregetom.graphwiki.graph

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = [
    "cz.gregetom.graphwiki.graph",
    "cz.gregetom.graphwiki.commons.security",
    "cz.gregetom.graphwiki.commons.web"
])
@EnableJpaRepositories(basePackages = ["cz.gregetom.graphwiki.graph.dao.jpa.repository"])
@EntityScan(basePackages = ["cz.gregetom.graphwiki.graph.dao.jpa.data"])
class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}
