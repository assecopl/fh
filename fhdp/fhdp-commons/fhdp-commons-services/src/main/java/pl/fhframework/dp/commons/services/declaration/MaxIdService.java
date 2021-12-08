package pl.fhframework.dp.commons.services.declaration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class MaxIdService {

    @Autowired
    MongoTemplate mongoTemplate;

    public Integer getMaxId(String key) throws InterruptedException {
        Query query = new Query(Criteria.where("key").is(key));
        Update update = new Update().inc("value", 1);
        MaxIdDto result = mongoTemplate.findById(key, MaxIdDto.class);
        if(result == null) {
            result = new MaxIdDto(key);
            mongoTemplate.save(result);
        }
        MaxIdDto newestValue = mongoTemplate.update(MaxIdDto.class)
                .matching(query)
                .apply(update)
                .withOptions(FindAndModifyOptions.options().returnNew(true)) // Now return the newly updated document when updating
                .findAndModifyValue();
        return newestValue.getValue();
    }

}
