package pl.fhframework.dp.commons.ds.repository.mongo.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
public class HistoryRepositoryDocument extends RepositoryDocument {
	public HistoryRepositoryDocument(RepositoryDocument doc, String historyId) {
		this.metadata = doc.getMetadata();
		this.operation = doc.getOperation();
		this.id = historyId;
		this.documentId = doc.getId();
		this.created = doc.getCreated();
		this.modified = doc.getModified();
		this.version = doc.getVersion();
		this.historyContentId = doc.getHistoryContentId();
	}
	public HistoryRepositoryDocument() {
	}
	String documentId;
	String contentId;
	boolean changedContent = false;
}
