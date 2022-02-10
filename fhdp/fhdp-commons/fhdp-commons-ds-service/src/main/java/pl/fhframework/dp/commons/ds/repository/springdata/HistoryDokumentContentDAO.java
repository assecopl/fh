package pl.fhframework.dp.commons.ds.repository.springdata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.fhframework.dp.commons.ds.repository.mongo.model.HistoryDocumentContent;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
@Component
public class HistoryDokumentContentDAO extends BaseDAO<HistoryDocumentContent> {

	@Value("${drs.history.document.content.collection.name:fhdp_history_document_content}")
	private String collectionName;
	
	@Override
	protected String getCollectionName() {
		return "icdts_history_document_content";
	}



	@Override
	protected Class<HistoryDocumentContent> getObjectClass() {
		return HistoryDocumentContent.class;
	}
	

	
    
}
