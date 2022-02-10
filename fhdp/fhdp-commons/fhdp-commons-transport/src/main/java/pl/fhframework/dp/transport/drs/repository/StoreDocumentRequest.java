package pl.fhframework.dp.transport.drs.repository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.fhframework.dp.transport.drs.BaseRequest;
import pl.fhframework.dp.transport.drs.repository.Document;

@NoArgsConstructor
@AllArgsConstructor
public class StoreDocumentRequest extends BaseRequest {
	protected pl.fhframework.dp.transport.drs.repository.Document document;

	public pl.fhframework.dp.transport.drs.repository.Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
}
