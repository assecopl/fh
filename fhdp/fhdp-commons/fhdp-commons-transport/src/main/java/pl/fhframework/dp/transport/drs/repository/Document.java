package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.repository.Metadata;
import pl.fhframework.dp.transport.drs.repository.Operation;

import java.time.LocalDateTime;

@Getter @Setter
public class Document {
	protected String id;
	protected Metadata metadata;
	protected Operation operation;
	protected byte[] content;
	protected int version;
	protected LocalDateTime created;
	protected LocalDateTime modified;
	protected Long dbId;
}
