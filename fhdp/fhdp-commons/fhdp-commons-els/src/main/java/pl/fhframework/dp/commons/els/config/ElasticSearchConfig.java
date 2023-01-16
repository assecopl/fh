package pl.fhframework.dp.commons.els.config;

import lombok.Getter;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version $Revision:  $, $Date:  $
 * @created 2019-02-15
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = {"pl.fhframework.dp"})
@ComponentScan(value = "pl.fhframework.dp.commons.els.config")
public class ElasticSearchConfig extends ElasticsearchConfigurationSupport {
    private final Logger logger = LoggerFactory.getLogger(ElasticSearchConfig.class);

    @Autowired
    private ElasticSearchParams elasticSearchParams;

    @Bean
    public String indexNamePrefix() {
        return elasticSearchParams.getIndexNamePrefix();
    }

    @Bean
    public RestHighLevelClient elasticsearchClient() throws Exception {
        return elasticSearchParams.elasticsearchClient();
    }

    @Bean
    ReactiveElasticsearchClient client() {
        return elasticSearchParams.client();
    }

    //@Bean
    //public ElasticsearchOperations elasticsearchTemplate(RestHighLevelClient elasticsearchClient) throws Exception{
    //    return new ElasticsearchRestTemplate(elasticsearchClient);
    //}


//    @WritingConverter
//    public class LocalDateTimeWritingConverter implements Converter<LocalDateTime, String> {
//
//        @Override
//        public String convert(LocalDateTime source) {
//            return source.format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSX"));
//        }
//    }
//
//    @ReadingConverter
//    public class LocalDateTimeReadingConverter implements Converter<String, LocalDateTime> {
//
//        @Override
//        public LocalDateTime convert(String source) {
//            return LocalDateTime.parse(source, DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSX"));
//        }
//    }
//
//    @WritingConverter
//    public class LocalDateWritingConverter implements Converter<LocalDate, String>  {
//
//        @Override
//        public String convert(LocalDate source) {
//            return source.format(DateTimeFormatter.ofPattern("uuuu-MM-dd"));
//        }
//    }
//
//    @ReadingConverter
//    public class LocalDateReadingConverter implements Converter<String, LocalDate>  {
//
//        @Override
//        public LocalDate convert(String source) {
//            return LocalDate.parse(source, DateTimeFormatter.ofPattern("uuuu-MM-dd"));
//        }
//    }
//
//    @Bean
//    @Override
//    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
//        return new ElasticsearchCustomConversions(Arrays.asList(new LocalDateTimeWritingConverter(),
//                new LocalDateTimeReadingConverter(), new LocalDateReadingConverter(),
//                new LocalDateWritingConverter()));
//    }
}

