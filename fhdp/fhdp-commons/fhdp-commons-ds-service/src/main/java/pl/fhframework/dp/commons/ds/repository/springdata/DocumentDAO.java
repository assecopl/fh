package pl.fhframework.dp.commons.ds.repository.springdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Component;

import com.mongodb.client.ClientSession;

import pl.fhframework.dp.commons.ds.repository.mongo.model.RepositoryDocument;
import pl.fhframework.dp.transport.drs.repository.FindDocumentRequest;
import pl.fhframework.dp.transport.drs.repository.Metadata;
import pl.fhframework.dp.transport.drs.repository.OtherMetadata;
import pl.fhframework.dp.transport.drs.repository.UpdateDocumentRequest;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;


@Component
public class DocumentDAO extends BaseDAO<RepositoryDocument> {

	@Value("${drs.document.collection.name:fhdp_document}")
	private String collectionName;

	public DocumentDAO() {
		super();
	}

	public DocumentDAO(MongoTemplate mongoTemplate, ClientSession session, String collectionName) {
		super(mongoTemplate, session);
		this.collectionName = collectionName;
	}
	
	public DocumentDAO getSessionInstance(ClientSession session) {
		return new DocumentDAO(mongoTemplate, session, collectionName);
	}	

	protected String getCollectionName() {
		return collectionName;
	}
	
	@Override
	protected Class<RepositoryDocument> getObjectClass() {
		// TODO Auto-generated method stub
		return RepositoryDocument.class;
	}	

	public void storeItem(RepositoryDocument item) {
		 mongoTemplate.save(item, getCollectionName());
	}
	
    public boolean checkIfExists(String id, int version) {
    	
    	List<RepositoryDocument> result = mongoTemplate.query(RepositoryDocument.class).inCollection(getCollectionName())
    			  .matching(query(where("id").is(id).and("version").is(version))).all();
    	if(result.size()>0) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public RepositoryDocument getByDbId(Long dbId) {
		List<RepositoryDocument> result = mongoTemplate.query(RepositoryDocument.class).inCollection(getCollectionName())
				.matching(query(where("dbId").is(dbId))).all();
		if(result.isEmpty()) return null;
		else return result.get(0);
	}
 
  
	public void updateVersion(String id, int version) {
		
		Query query = new Query();
		query.addCriteria(where("id").is(id).and("version").is(version));
		UpdateDefinition update = new Update().inc("version", 1);
		RepositoryDocument result = mongoTemplate.update(RepositoryDocument.class)
				.inCollection(getCollectionName())
                .matching(query)
                .apply(update)
                .withOptions(FindAndModifyOptions.options().returnNew(true)) // Now return the newly updated document when updating
                .findAndModifyValue();
		
        if(result==null) {
        	throw new RuntimeException("version mismatch");
        }
		
	}

	public void updateDocument(UpdateDocumentRequest request) {
		Query query = new Query();
		query.addCriteria(where("_id").is(request.getId()));
    	if(request.getOtherMetadata()!=null) {
	    	for(OtherMetadata omd : request.getOtherMetadata()) {
	    		//updateOtherMetadata(collection, request.getId(), omd.getName(), omd.getValue());
	    		Update updateOMD = new Update();
	    		updateOMD = updateOMD.pull("metadata.otherMetadata", Query.query(Criteria.where("name").is(omd.getName())));
	    		mongoTemplate.updateFirst(query, updateOMD, getCollectionName());
	    		Update updatePush = new Update();
	    		updatePush = updatePush.push("metadata.otherMetadata", omd);
	    		mongoTemplate.updateFirst(query, updatePush, getCollectionName());
	    	}
    	}
		Update update = new Update();
    	if(request.getContentType()!=null) {
    		update = update.set("metadata.contentType", request.getContentType());
    	}
    	if(request.getLocalReferenceNumber()!=null) {
    		update = update.set("metadata.localReferenceNumber", request.getLocalReferenceNumber());
    	}
    	update = update.set("modified", new Date());
        if(request.getOperation()!=null) {
        	update = update.set("operation.name", request.getOperation().getName());
        	update = update.set("operation.description", request.getOperation().getDescription());
        }
        additionalUpdates(update, request);
		RepositoryDocument result = mongoTemplate.update(RepositoryDocument.class)
				.inCollection(getCollectionName())
                .matching(query)
                .apply(update)
                .withOptions(FindAndModifyOptions.options().returnNew(true)) // Now return the newly updated document when updating
                .findAndModifyValue();
	}

	protected void additionalUpdates(Update update, UpdateDocumentRequest request) {
	}

	public List<RepositoryDocument> find(FindDocumentRequest request) {
		
		Query query = new Query();
		
		Metadata md = request.getMetadata();
		if(md!=null) {
			if(md.getContentType()!=null) {
				query.addCriteria(where("metadata.contentType").is(md.getContentType()));
			}
			if(md.getLocalReferenceNumber()!=null) {
				query.addCriteria(where("metadata.localReferenceNumber").is(md.getLocalReferenceNumber()));
			}
		}
		
		if(request.getMetadata()!=null && request.getMetadata().getOtherMetadata() != null) {
			for(OtherMetadata omd : request.getMetadata().getOtherMetadata()) {
				query.addCriteria(Criteria.where("metadata.otherMetadata").elemMatch(Criteria.where("name").is(omd.getName()).and("value").is(omd.getValue())));
			}
		}
		
		List<RepositoryDocument> result = mongoTemplate.find(query, getObjectClass(), getCollectionName());
		
		
		return result;
	}

    
}
