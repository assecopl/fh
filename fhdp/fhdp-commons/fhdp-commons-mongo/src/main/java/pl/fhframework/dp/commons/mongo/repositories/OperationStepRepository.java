package pl.fhframework.dp.commons.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.fhframework.dp.commons.mongo.entities.OperationStep;

public interface OperationStepRepository extends MongoRepository<OperationStep, String> {

}
