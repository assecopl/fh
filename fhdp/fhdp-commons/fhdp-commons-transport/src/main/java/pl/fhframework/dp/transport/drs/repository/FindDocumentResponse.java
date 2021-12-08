package pl.fhframework.dp.transport.drs.repository;

import pl.fhframework.dp.transport.drs.BaseResponse;

import java.util.List;

public class FindDocumentResponse extends BaseResponse {
	protected List<Document> documents;

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
}
