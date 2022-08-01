package pl.fhframework.dp.commons.ds.repository.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.ds.repository.mongo.model.MaxIdDto;
import pl.fhframework.dp.commons.ds.repository.mongo.model.MaxIdDtoLong;
import pl.fhframework.dp.transport.service.IMaxIdService;
import pl.fhframework.dp.transport.service.IMaxIdServiceLong;


@Service
@Slf4j
public class MaxIdServiceLong implements IMaxIdServiceLong {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Long getMaxId(String key) throws InterruptedException {
        Query query = new Query(Criteria.where("key").is(key));
        Update update = new Update().inc("value", 1L);
        MaxIdDtoLong result = mongoTemplate.findById(key, MaxIdDtoLong.class);
        if(result == null) {
            result = new MaxIdDtoLong(key);
            mongoTemplate.save(result);
        }
        MaxIdDtoLong newestValue = mongoTemplate.update(MaxIdDtoLong.class)
                .matching(query)
                .apply(update)
                .withOptions(FindAndModifyOptions.options().returnNew(true)) // Now return the newly updated document when updating
                .findAndModifyValue();
        return newestValue.getValue();
    }

}
