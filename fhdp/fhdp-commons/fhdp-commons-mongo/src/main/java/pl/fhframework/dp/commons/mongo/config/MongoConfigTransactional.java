package pl.fhframework.dp.commons.mongo.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Configuration
@Deprecated
@Slf4j
public class MongoConfigTransactional extends AbstractMongoClientConfiguration {

    @Value("${mongo.hostAndPort:localhost:27017}")
    private String hostAndPort;
    @Value("${mongo.database:fhdp-repository}")
    private String dbName;
    @Value("${mongo.client.pool.minSize:10}")
    private int minSize;
    @Value("${mongo.client.pool.maxSize:30}")
    private int maxSize;
    @Value("${mongo.autoindex:false}")
    private Boolean autoIndex;

    
    @Bean
    public MongoClient mongoClient() {
        return super.mongoClient();
    }

    @Bean
    MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        log.info("***** Initiating obsolete MongoTransactionManager...");
        return new MongoTransactionManager(dbFactory);
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        String[] hosts = hostAndPort.split(Pattern.quote(","));
        builder
                .applyToClusterSettings(settings  -> {
                    settings.hosts(serversList(hosts));
                })
                .applyToConnectionPoolSettings(poolSettings -> {
                    poolSettings.minSize(minSize)
                            .maxSize(maxSize);
                });
    }

    private List<ServerAddress> serversList(String[] hosts) {
        List<ServerAddress> ret = new ArrayList<>();
        for(String host: hosts) {
            String[] elements = host.split(Pattern.quote(":"));
            ServerAddress address = new ServerAddress(elements[0], Integer.parseInt(elements[1]));
            ret.add(address);
        }
        return ret;
    }


    @Override
    protected String getDatabaseName() {
        return dbName;
    }
    
    @Override
    protected boolean autoIndexCreation() {
    	System.out.println("autoIndexCreation: " + autoIndex);
        return autoIndex;
    }    
    
}
