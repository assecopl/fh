package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.BaseResponse;

@Getter @Setter
public class StoreDocumentResponse extends BaseResponse {
	Document document;
//	protected String id;
//	protected Metadata metadata;
//	protected Operation operation;
//	protected int version;
//	protected Date created;
//	protected Date modified;
}
