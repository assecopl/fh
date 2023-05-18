package pl.fhframework.dp.commons.ds.repository.mongo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;
import pl.fhframework.dp.transport.drs.repository.Metadata;
import pl.fhframework.dp.transport.drs.repository.Operation;

import java.time.LocalDateTime;

@Getter @Setter 
public class RepositoryDocument {
	@MongoId
	protected String id;
	@Indexed(unique = true, sparse = true)
	protected Long dbId;
	protected Metadata metadata;
	protected Operation operation;
	protected int version;
	protected LocalDateTime created;
	protected LocalDateTime modified;
	protected String historyContentId;
	
	protected long contentSize;
	protected String contentMD5;
	
	
}
