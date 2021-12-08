package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DocumentHistory extends Document {
	boolean changedContent = false;
}
