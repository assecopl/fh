package pl.fhframework.dp.commons.ds.repository.springdata;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.TransactionBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.IOException;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;


public abstract class BaseDAO<T> {
	@Autowired
    protected MongoClient mongoClient;
    
	@Autowired
    protected MongoTemplate mongoTemplate;

	public BaseDAO(MongoTemplate mongoTemplate, ClientSession session) {
		this.mongoTemplate = mongoTemplate.withSession(session);
		this.session = session;
	}    
	
	public BaseDAO() {
		
	}
    
    
    protected abstract String getCollectionName();
    protected abstract Class<T> getObjectClass();
    protected ClientSession session = null;
    public abstract BaseDAO<T> getSessionInstance(ClientSession session);
  
    public void storeItem(T item) throws JsonProcessingException {
    	mongoTemplate.save(item, getCollectionName());
    }

    public boolean checkIfExists(String id) {
    	T result = mongoTemplate.findById(id, getObjectClass(), getCollectionName());
    	if(result!=null) {
    		return true;
    	} else {
    		return false;
    	}
    }   
    


    public void updateObject(T item) throws JsonProcessingException {
    	mongoTemplate.save(item, getCollectionName());
    } 
    public void replaceObject(T item) throws JsonProcessingException {
    	mongoTemplate.save(item, getCollectionName());
    }
    public void replaceObject(T item, boolean upsert) throws JsonProcessingException {
    	mongoTemplate.save(item, getCollectionName());  
    } 
    

    public void updateObjectProperty(String objectId, String name, Object value) {
    	
		Query query = new Query();
		query.addCriteria(where("_id").is(objectId));
		Update update = new Update();
    	update = update.set(name, value);
    	
		T result = mongoTemplate.update(getObjectClass())
				.inCollection(getCollectionName())
                .matching(query)
                .apply(update)
                .withOptions(FindAndModifyOptions.options().returnNew(true)) // Now return the newly updated document when updating
                .findAndModifyValue();     	
    }          
    
    
    public void deleteObject(String objectID) throws JsonParseException, JsonMappingException, IOException {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(objectID));
        T result = mongoTemplate.findAndRemove(query, getObjectClass(), getCollectionName());
    }      
    
    public List<T> find(Query query){
    	List<T> result = null;
    	result = mongoTemplate.find(query, getObjectClass(), getCollectionName());
    	
    	return result;
    }
    
    public T getObject(String objectID) throws JsonParseException, JsonMappingException, IOException {
    	return mongoTemplate.findById(objectID, getObjectClass(), getCollectionName());
    }         
    

   protected ClientSession getSession() {
	   return session;
   }


    public ClientSession getNewSession() {
    	return mongoClient.startSession();
    }
    
    protected TransactionOptions getTXOptions() {
		TransactionOptions txnOptions = TransactionOptions.builder()
		        .readPreference(ReadPreference.primary())
		        .readConcern(ReadConcern.LOCAL)
		        .writeConcern(WriteConcern.ACKNOWLEDGED)
		        .build();
		
		return txnOptions;
    }
    
	public void withTransaction(TransactionBody<String> txnBody) {
		try {
			session.withTransaction(txnBody, getTXOptions());
		} finally {
			if(session!=null) {
				session.close();
			}
		}
	}	
    
}
