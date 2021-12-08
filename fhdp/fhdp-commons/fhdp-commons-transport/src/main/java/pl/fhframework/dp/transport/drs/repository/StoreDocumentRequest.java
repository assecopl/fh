package pl.fhframework.dp.transport.drs.repository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.fhframework.dp.transport.drs.BaseRequest;

@NoArgsConstructor
@AllArgsConstructor
public class StoreDocumentRequest extends BaseRequest {
	protected Document document;

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
}
