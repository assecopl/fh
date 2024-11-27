package pl.fhframework.dp.commons.ds.repository.springdata;

import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ClientSession;

import pl.fhframework.dp.commons.ds.repository.mongo.model.DocumentContent;
import pl.fhframework.dp.commons.ds.repository.mongo.model.HistoryDocumentContent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
@Component
public class HistoryDokumentContentDAO extends BaseDAO<HistoryDocumentContent> {

	protected int CHUNK_SIZE = 15000000;
	
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

	protected String getChunkedCollectionName() {
		return getCollectionName()+"_chunked";
	}	

	@Override
	protected Class<HistoryDocumentContent> getObjectClass() {
		return HistoryDocumentContent.class;
	}

	@Override
	public HistoryDokumentContentDAO getSessionInstance(ClientSession session) {
		return new HistoryDokumentContentDAO(mongoTemplate, session, collectionName);
	}
	
	@Override
    public void storeItem(HistoryDocumentContent item) throws JsonProcessingException {
    	
    	
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("_id", item.getId());
		
		byte[] content = item.getContent();
		
		dbObject.put("size", content.length);
		dbObject.put("documentId", item.getDocumentId());
		dbObject.put("documentVersion", item.getDocumentVersion());
		content = item.getContent();

		if(content.length>CHUNK_SIZE) {
			dbObject.put("chunked", true);
			BasicDBObject chunks = new BasicDBObject();
			
			int chunksize = CHUNK_SIZE;
			
		    int start = 0;
		    int chunkNr = 1;
		    while (start < content.length) {
		        int end = Math.min(content.length, start + chunksize);
		        byte[] chunkContent = Arrays.copyOfRange(content, start, end);
		        
		        String chunkID = UUID.randomUUID().toString();
		        BasicDBObject chunk = new BasicDBObject();
		        chunk.put("_id", chunkID);
		        chunk.put("chunkNr", chunkNr);
		        chunk.put("contentId", dbObject.getString("_id"));
				Binary cbinary = new Binary(chunkContent);
		        chunk.put("content", cbinary);
		        
		        chunks.put("c"+chunkNr, chunkID);
		    	mongoTemplate.save(chunk, getChunkedCollectionName());
		        
		        start += chunksize;
		        chunkNr++;
		        
		    }
	        dbObject.put("chunks", chunks);
			
		} else {
			Binary binary = new Binary(content);
			dbObject.put("content", binary);
		}    	
    	
    	mongoTemplate.save(dbObject, getCollectionName());
    }
	
	@Override
    public HistoryDocumentContent getObject(String objectID) throws IOException {
    	String id = objectID;
    	BasicDBObject dbObject = mongoTemplate.findById(objectID, BasicDBObject.class, getCollectionName());        
        
    	if(dbObject==null) {
    		return null;
    	}
		
    	HistoryDocumentContent result = new HistoryDocumentContent();
		result.setId(id);
//		String documentId;
//		int documentVersion;		
		result.setDocumentId(dbObject.getString("documentId"));
		result.setDocumentVersion(dbObject.getInt("documentVersion",-1));
		
		byte[] content = null;
		Boolean chunked = dbObject.getBoolean("chunked");

		if(chunked!=null && chunked) {
			Document chunks = (Document) dbObject.get("chunks");
		    int chunkNr = 1;
		    String chunkId = chunks.getString("c"+chunkNr);
	        BasicDBObject chunk = mongoTemplate.findById(chunkId, BasicDBObject.class, getChunkedCollectionName());
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        while(chunk!=null) {
	        	Binary chunkContent = (Binary) chunk.get("content");
	        	baos.write(chunkContent.getData());
	        	
	        	chunkNr++;
			    chunkId = chunks.getString("c"+chunkNr);
			    if(chunkId!=null) {
			    	chunk = mongoTemplate.findById(chunkId, BasicDBObject.class, getChunkedCollectionName());
			    } else {
			    	chunk = null;
			    }
	        	
	        }
	        content = baos.toByteArray();
		} else {
			Binary bContent = (Binary) dbObject.get("content");
			content = bContent.getData();
		}
		
		
		
//		Boolean compressed = dbObject.getBoolean("compressed");
//		if(compressed) {
//			content = uncompress(content);
//		} else {
//		}
		result.setContent(content);
		
		return result;
        
        
    }   
	
    
}
