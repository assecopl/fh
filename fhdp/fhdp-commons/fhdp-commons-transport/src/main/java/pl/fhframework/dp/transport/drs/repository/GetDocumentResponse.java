package pl.fhframework.dp.transport.drs.repository;

import pl.fhframework.dp.transport.drs.BaseResponse;
import pl.fhframework.dp.transport.drs.repository.Document;

public class GetDocumentResponse extends BaseResponse {
	protected pl.fhframework.dp.transport.drs.repository.Document document;

	public pl.fhframework.dp.transport.drs.repository.Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
}
