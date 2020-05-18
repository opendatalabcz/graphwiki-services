package cz.gregetom.graphwiki.user

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = [
    "cz.gregetom.graphwiki.user",
    "cz.gregetom.graphwiki.commons.security",
    "cz.gregetom.graphwiki.commons.web"
])
@EnableJpaRepositories(basePackages = ["cz.gregetom.graphwiki.user.dao.repository"])
@EntityScan(basePackages = ["cz.gregetom.graphwiki.user.dao.data"])
class Application {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}
