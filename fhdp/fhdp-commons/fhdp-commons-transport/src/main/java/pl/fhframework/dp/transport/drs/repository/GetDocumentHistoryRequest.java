package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.BaseRequest;

@Getter @Setter
public class GetDocumentHistoryRequest extends BaseRequest {
	protected String id;
	protected boolean withContent = false;
	protected Integer version = null;
	protected String[] operationName = null;
	protected boolean latest = false;
}
