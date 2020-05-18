package cz.gregetom.graphwiki.graph.config

import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoMapper
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerIoRegistryV2d0
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy

@Configuration
class JanusGraphConfig(
        @Value("\${graphwiki.janusgraph.datasource.host}")
        private val hosts: List<String>,
        @Value("\${graphwiki.janusgraph.datasource.port}")
        private val port: Int,
        @Value("\${graphwiki.janusgraph.datasource.traversal-source-name}")
        private val traversalSourceName: String
) {

    private lateinit var datasource: GraphTraversalSource

    @Bean
    fun janusgraphTraversalSource(): GraphTraversalSource {

        /**
         * Configure connection to JanusGraph database.
         */
        // GryoMessageSerializerV1d0 - https://docs.janusgraph.org/connecting/java/
        @Suppress("DEPRECATION")
        val cluster = Cluster.build()
                .addContactPoints(*hosts.toTypedArray())
                .port(port)
                .serializer(
                        GryoMessageSerializerV3d0(GryoMapper.build()
                                .addRegistry(JanusGraphIoRegistry.getInstance())
                                .addRegistry(TinkerIoRegistryV2d0.instance())
                        )
                )
                .create()
        return AnonymousTraversalSource
                .traversal()
                .withRemote(DriverRemoteConnection.using(cluster, traversalSourceName))
                .apply { datasource = this }
    }

    @PreDestroy
    fun preDestroy() {
        this.datasource.close()
    }
}
