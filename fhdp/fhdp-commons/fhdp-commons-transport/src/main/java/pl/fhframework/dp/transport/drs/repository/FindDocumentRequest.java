package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.BaseRequest;
import pl.fhframework.dp.transport.drs.repository.Metadata;

@Getter @Setter
public class FindDocumentRequest extends BaseRequest {
	
	Metadata metadata;
	

}
