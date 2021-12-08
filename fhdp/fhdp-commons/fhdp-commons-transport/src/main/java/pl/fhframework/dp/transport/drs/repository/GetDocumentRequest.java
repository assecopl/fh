package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.BaseRequest;

@Getter @Setter
public class GetDocumentRequest extends BaseRequest {
	protected String id;
	protected Long dbId;
}
