package pl.fhframework.dp.transport.drs.repository;

import pl.fhframework.dp.transport.drs.BaseResponse;
import pl.fhframework.dp.transport.drs.repository.Document;

import java.util.List;

public class FindDocumentResponse extends BaseResponse {
	protected List<pl.fhframework.dp.transport.drs.repository.Document> documents;

	public List<pl.fhframework.dp.transport.drs.repository.Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
}
