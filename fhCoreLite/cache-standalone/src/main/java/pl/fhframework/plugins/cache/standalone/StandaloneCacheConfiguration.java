package pl.fhframework.plugins.cache.standalone;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.configuration.global.TransportConfigurationBuilder;
import org.infinispan.eviction.EvictionType;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.configuration.FHConfiguration;

import java.util.concurrent.TimeUnit;


@Configuration
public class StandaloneCacheConfiguration {

    @Autowired
    private FHConfiguration fhConfiguration;

    @Bean
    public EmbeddedCacheManager infiniSpanCacheManager(@Qualifier("asyncReplicationConfig") org.infinispan.configuration.cache.Configuration asyncReplicationConfig) {
        String clusterName = fhConfiguration.getClusterName();
        FhLogger.info(this.getClass(), "Using infinispan cache cluster name: " + clusterName);
        TransportConfigurationBuilder transportConfigurationBuilder =
                new GlobalConfigurationBuilder()
                        .transport()
                        .clusterName(clusterName)
                        .defaultTransport();
        if (getClass().getClassLoader().getResource("jgroups.xml") != null) {
            transportConfigurationBuilder.addProperty("configurationFile", "jgroups.xml");
        }
        GlobalConfiguration conf = transportConfigurationBuilder.build();

        

        return new DefaultCacheManager(
                conf,
                asyncReplicationConfig);
    }

    @Bean(name = "asyncReplicationConfig")
    public org.infinispan.configuration.cache.Configuration asyncReplicationConfig() {
        return new ConfigurationBuilder()
                .clustering()
                .cacheMode(CacheMode.REPL_ASYNC)
                .eviction().type(EvictionType.COUNT).size(10000)
                .expiration().lifespan(10, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public Cache<String, String> loginsWithWebSocketCache(@Autowired org.infinispan.manager.EmbeddedCacheManager container, @Qualifier("asyncReplicationConfig") org.infinispan.configuration.cache.Configuration asyncReplicationConfig) {
        final String CACHE_NAME = "loginsWithWebSocket";
        container.defineConfiguration(CACHE_NAME, asyncReplicationConfig);

        return container.getCache(CACHE_NAME);
    }

    @Bean
    public Cache<String, Long> userLogouts(@Autowired org.infinispan.manager.EmbeddedCacheManager container, @Qualifier("asyncReplicationConfig") org.infinispan.configuration.cache.Configuration asyncReplicationConfig) {
        final String CACHE_NAME = "userLogouts";
        container.defineConfiguration(CACHE_NAME, asyncReplicationConfig);
        return container.getCache(CACHE_NAME);
    }

}
