package pl.fhframework.dp.commons.ds.repository.springdata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.ClientSession;

import pl.fhframework.dp.commons.ds.repository.mongo.model.HistoryRepositoryDocument;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
@Component
public class HistoryDocumentDAO extends BaseDAO<HistoryRepositoryDocument> {

	@Value("${drs.history.document.collection.name:fhdp_history_document}")
	private String collectionName;
	
	public HistoryDocumentDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HistoryDocumentDAO(MongoTemplate mongoTemplate, ClientSession session, String collectionName) {
		super(mongoTemplate, session);
		this.collectionName = collectionName;
	}

	@Override
	protected String getCollectionName() {
		return collectionName;
	}

	@Override
	protected Class<HistoryRepositoryDocument> getObjectClass() {
		return HistoryRepositoryDocument.class;
	}



	@Override
	public HistoryDocumentDAO getSessionInstance(ClientSession session) {
		return new HistoryDocumentDAO(mongoTemplate, session, collectionName);
	}
	
    
//  
//	public void updateVersion(String id, int version) {
//		MongoCollection<BasicDBObject> collection;
//    	collection = db.getCollection(getCollectionName(), BasicDBObject.class);
//    	
//    	Bson filter = Filters.and(Filters.eq("_id", id),Filters.eq("version", version));
//        BasicDBObject update = new BasicDBObject();
//        update.put("$inc",  new BasicDBObject("version", 1));
//        BasicDBObject result = collection.findOneAndUpdate(filter, update);
//        if(result==null) {
//        	throw new RuntimeException("version mismatch");
//        }
//	}
//
//	public void updateDocument(UpdateDocumentRequest request) {
//		MongoCollection<BasicDBObject> collection;
//    	collection = db.getCollection(getCollectionName(), BasicDBObject.class);
//    	
//    	Bson filter = Filters.eq("_id", request.getId());
//		updateVersion(request.getId(), request.getVersion());
//        BasicDBObject update = new BasicDBObject();
//        update.put("$set",  new BasicDBObject("modified", new Date()));
//        collection.findOneAndUpdate(filter, update);    
//	}
    
}
