package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.repository.Document;

@Getter @Setter
public class DocumentHistory extends Document {
	boolean changedContent = false;
}
