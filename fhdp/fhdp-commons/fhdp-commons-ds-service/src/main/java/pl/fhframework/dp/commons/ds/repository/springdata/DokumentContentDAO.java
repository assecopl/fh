package pl.fhframework.dp.commons.ds.repository.springdata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.ClientSession;

import pl.fhframework.dp.commons.ds.repository.mongo.model.DocumentContent;


@Component
public class DokumentContentDAO extends BaseDAO<DocumentContent> {

	public DokumentContentDAO() {
		super();
	}

	public DokumentContentDAO(MongoTemplate mongoTemplate, ClientSession session, String collectionName) {
		super(mongoTemplate, session);
		this.collectionName = collectionName;
	}

	@Value("${drs.document.content.collection.name:fhdp_document_content}")
	private String collectionName;
	
	@Override
	protected String getCollectionName() {
		return collectionName;
	}


	@Override
	protected Class<DocumentContent> getObjectClass() {
		return DocumentContent.class;
	}

	@Override
	public DokumentContentDAO getSessionInstance(ClientSession session) {
		return new DokumentContentDAO(mongoTemplate, session, collectionName);
	}
	

	
//	@Override
//	public void storeItem(DocumentContent item) throws JsonProcessingException {
//    	MongoCollection<BasicDBObject> collection;
//    	collection = db.getCollection(getCollectionName(), BasicDBObject.class);
//       	collection.insertOne(objectToDB(item));
//	}
	

	
    
}
