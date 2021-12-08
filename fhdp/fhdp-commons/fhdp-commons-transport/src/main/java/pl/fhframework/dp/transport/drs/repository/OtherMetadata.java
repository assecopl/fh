package pl.fhframework.dp.transport.drs.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Builder
@Getter @Setter
public class OtherMetadata{
	@Field(type = FieldType.Text)
	protected String name;
	@Field(type = FieldType.Text)
	protected String value;

	public OtherMetadata() {
	}

	public OtherMetadata(String name, String value) {
		this.name = name;
		this.value = value;
	}
}


