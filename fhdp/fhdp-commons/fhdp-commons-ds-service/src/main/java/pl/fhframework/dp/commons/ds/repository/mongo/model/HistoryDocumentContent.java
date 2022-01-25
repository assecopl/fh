package pl.fhframework.dp.commons.ds.repository.mongo.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HistoryDocumentContent extends DocumentContent {
	String documentId;
	int documentVersion;
}
