package pl.fhframework.dp.transport.drs.repository;

import pl.fhframework.dp.transport.drs.BaseResponse;

public class GetDocumentResponse extends BaseResponse {
	protected Document document;

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
}
