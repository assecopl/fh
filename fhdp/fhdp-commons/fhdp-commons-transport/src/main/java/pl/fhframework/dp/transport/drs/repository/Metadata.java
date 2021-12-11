package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Metadata {
	protected String localReferenceNumber;
	protected String contentType;
	@Field(type = FieldType.Nested)
	protected List<OtherMetadata> otherMetadata = new ArrayList<>();
}
