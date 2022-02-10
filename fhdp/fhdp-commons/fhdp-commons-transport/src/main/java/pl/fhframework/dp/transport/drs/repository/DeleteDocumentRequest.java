package pl.fhframework.dp.transport.drs.repository;

import pl.fhframework.dp.transport.drs.BaseRequest;

public class DeleteDocumentRequest extends BaseRequest {
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
