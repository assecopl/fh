package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pl.fhframework.dp.transport.drs.BaseRequest;

import java.util.List;

@Getter @Setter
public class UpdateDocumentRequest extends BaseRequest {
	
	protected String id;
	protected int version;
	protected byte[] content;
	protected Operation operation;
	
	
	protected String name;
	protected String localReferenceNumber;
	protected String customsReferenceNumber;
	protected String messageIdentification;
	protected String contentType;
	@Field(type = FieldType.Nested)
	protected List<OtherMetadata> otherMetadata;
	protected Correspondent sender;
	@Field(type = FieldType.Nested)
	protected List<Correspondent> recipients;
	protected String responseTo;
	protected String attachmentTo;	
	
	
	protected boolean overwrite = false;
}
