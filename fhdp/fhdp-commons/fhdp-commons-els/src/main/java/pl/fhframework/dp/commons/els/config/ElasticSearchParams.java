package pl.fhframework.dp.commons.els.config;

import lombok.Getter;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ElasticSearchParams {
    private final Logger logger = LoggerFactory.getLogger(ElasticSearchParams.class);

    @Value("${elasticSearch.hostAndPort:localhost:9200}")
    private String hostAndPort;

    @Getter
    @Value("${elasticSearch.indexNamePrefix:}")
    private String indexNamePrefix;
    @Getter
    @Value("${elasticSearch.maxConnPerRoute:150}")
    private int maxConnPerRoute;
    @Getter
    @Value("${elasticSearch.maxConnTotal:300}")
    private int maxConnTotal;
    @Getter
    @Value("${elasticSearch.socketTimeout:60000}")
    private int socketTimeout;

    public RestHighLevelClient elasticsearchClient() throws Exception {

//        hostAndPort = System.getProperty("elasticSearch.hostAndPort", null);
        logger.info("******** Initial hostAndPort: {} (system property elasticSearch.hostAndPort)", hostAndPort);
        logger.info("******** Initial indexNamePrefix: {} (system property elasticSearch.indexNamePrefix)", indexNamePrefix);
        String [] hosts = hostAndPort.split(Pattern.quote(","));

        HttpHost[] httpHosts = createHosts(hosts);
        RestClientBuilder builder = RestClient.builder(httpHosts);
        builder.setRequestConfigCallback(
                requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(15000)
                        .setSocketTimeout(socketTimeout));
        builder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
            httpAsyncClientBuilder.setMaxConnPerRoute(maxConnPerRoute)
                    .setMaxConnTotal(maxConnTotal)
                    .setDefaultIOReactorConfig(IOReactorConfig.custom()
                            .setSoKeepAlive(true)
                            .build());
            return httpAsyncClientBuilder;
        });
        return new RestHighLevelClient(builder);
    }

    private HttpHost[] createHosts(String[] hosts) {
        List<HttpHost> list = new ArrayList<>();
        for(String host: hosts) {
            String[] elements = host.split(Pattern.quote(":"));
            list.add(new HttpHost(elements[0], Integer.parseInt(elements[1]), "http"));
        }
        return list.toArray(new HttpHost[0]);
    }

    public ReactiveElasticsearchClient client() {
        String [] hosts = hostAndPort.split(Pattern.quote(","));
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(hosts)
                .withWebClientConfigurer(webClient -> {
                    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                            .codecs(configurer -> configurer.defaultCodecs()
                                    .maxInMemorySize(-1))
                            .build();
                    return webClient.mutate().exchangeStrategies(exchangeStrategies).build();
                })
                .build();

        return ReactiveRestClients.create(clientConfiguration);
    }
}

