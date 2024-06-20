package pl.fhframework.dp.commons.mongo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import pl.fhframework.dp.commons.mongo.entities.OperationStep;

import javax.annotation.PostConstruct;

@Configuration
@DependsOn("mongoTemplate")
public class FhCollectionsConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        mongoTemplate.indexOps(OperationStep.class) // collection name string or .class
                .ensureIndex(
                        new Index().on("operationGUID", Sort.Direction.ASC)
                );
    }
}
