package pl.fhframework.dp.commons.ds.repository.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter @Setter
public class DocumentContent {
	@MongoId
	String id;
	byte[] content;	
}
