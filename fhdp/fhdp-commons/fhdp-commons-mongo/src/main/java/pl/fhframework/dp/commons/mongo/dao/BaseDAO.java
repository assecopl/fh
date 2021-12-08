package pl.fhframework.dp.commons.mongo.dao;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.TransactionBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import pl.fhframework.dp.commons.base.exception.AppMsgException;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
public abstract class BaseDAO<T> {
//    @Resource(lookup="java:/mongoOAK")
	@Autowired
    protected MongoClient mongoClient;
    
	@Autowired
    protected MongoTemplate mongoTemplate;

    
    
    protected abstract String getCollectionName();
 //   protected abstract String getObjectID(T object);
    protected abstract Class<T> getObjectClass();
  
    public void storeItem(T item) {
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
    


    public void updateObject(T item) {
    	mongoTemplate.save(item, getCollectionName());
    } 
    public void replaceObject(T item) {
    	mongoTemplate.save(item, getCollectionName());
    }
    public void replaceObject(T item, boolean upsert) {
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
    
    
    public void deleteObject(String objectID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(objectID));
        T result = mongoTemplate.findAndRemove(query, getObjectClass(), getCollectionName());
    }      
    
    public List<T> find(Query query){
    	List<T> result = null;
    	result = mongoTemplate.find(query, getObjectClass(), getCollectionName());
    	
    	return result;
    }
    
    public T getObject(String objectID) {
    	return mongoTemplate.findById(objectID, getObjectClass(), getCollectionName());
    }         

    protected ClientSession getSession() {
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

	public void withTransaction(TransactionBody<String> txnBody) throws AppMsgException {
		ClientSession session = getSession();
		try {
			session.withTransaction(txnBody, getTXOptions());
		} catch(Exception e) {
			e.printStackTrace();
			session.abortTransaction();
			throw new AppMsgException( e.getMessage());
		} finally {
			if(session!=null) {
				session.close();
			}
		}
	}

//	public void withTransaction(TransactionBody<String> txnBody) {
//		ClientSession session = getSession();
//		try {
//			getSession().withTransaction(txnBody, getTXOptions());
//		} finally {
//			if(session!=null) {
//				session.close();
//			}
//		}
//	}
    
}
