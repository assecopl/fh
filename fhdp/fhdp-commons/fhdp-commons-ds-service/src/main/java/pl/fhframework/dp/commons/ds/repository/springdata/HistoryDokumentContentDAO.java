package pl.fhframework.dp.commons.ds.repository.springdata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.ClientSession;

import pl.fhframework.dp.commons.ds.repository.mongo.model.HistoryDocumentContent;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
@Component
public class HistoryDokumentContentDAO extends BaseDAO<HistoryDocumentContent> {

	@Value("${drs.history.document.content.collection.name:fhdp_history_document_content}")
	private String collectionName;
	
	public HistoryDokumentContentDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HistoryDokumentContentDAO(MongoTemplate mongoTemplate, ClientSession session, String collectionName) {
		super(mongoTemplate, session);
		this.collectionName = collectionName;
	}



	@Override
	protected String getCollectionName() {
		return "icdts_history_document_content";
	}



	@Override
	protected Class<HistoryDocumentContent> getObjectClass() {
		return HistoryDocumentContent.class;
	}

	@Override
	public HistoryDokumentContentDAO getSessionInstance(ClientSession session) {
		return new HistoryDokumentContentDAO(mongoTemplate, session, collectionName);
	}
	

	
    
}
