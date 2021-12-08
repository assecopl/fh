package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Metadata {
	protected String name;
	protected String localReferenceNumber;
	protected String customsReferenceNumber;
	protected String messageIdentification;
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
	protected LocalDateTime date;
	protected String contentType;
	@Field(type = FieldType.Nested)
	protected List<OtherMetadata> otherMetadata = new ArrayList<>();
	protected Correspondent sender;
	@Field(type = FieldType.Nested)
	protected List<Correspondent> recipients = new ArrayList<>();
	protected String responseTo;
	protected String attachmentTo;
}
