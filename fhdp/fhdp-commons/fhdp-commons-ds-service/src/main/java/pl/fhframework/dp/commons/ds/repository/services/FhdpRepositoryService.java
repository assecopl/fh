package pl.fhframework.dp.commons.ds.repository.services;

import org.springframework.dao.DuplicateKeyException;
import com.mongodb.client.TransactionBody;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import pl.fhframework.dp.commons.ds.repository.exception.DuplicateKeyRuntimeException;
import pl.fhframework.dp.commons.ds.repository.mongo.model.DocumentContent;
import pl.fhframework.dp.commons.ds.repository.mongo.model.HistoryDocumentContent;
import pl.fhframework.dp.commons.ds.repository.mongo.model.HistoryRepositoryDocument;
import pl.fhframework.dp.commons.ds.repository.mongo.model.RepositoryDocument;
import pl.fhframework.dp.commons.ds.repository.springdata.DocumentDAO;
import pl.fhframework.dp.commons.ds.repository.springdata.DokumentContentDAO;
import pl.fhframework.dp.commons.ds.repository.springdata.HistoryDocumentDAO;
import pl.fhframework.dp.commons.ds.repository.springdata.HistoryDokumentContentDAO;
import pl.fhframework.dp.transport.drs.Result;
import pl.fhframework.dp.transport.drs.repository.*;
import pl.fhframework.dp.transport.service.IRepositoryService;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@ApplicationScoped
@Component
public class FhdpRepositoryService implements IRepositoryService {
	
	private static final Logger log = LoggerFactory.getLogger(FhdpRepositoryService.class);
	
	@Autowired
	protected DokumentContentDAO dokumentContentDAO;
	
	@Autowired
	protected DocumentDAO documentDAO;

	@Autowired
	protected HistoryDokumentContentDAO hDokumentContentDAO;
	
	@Autowired
	protected HistoryDocumentDAO hDocumentDAO;
	
	@Override
	public StoreDocumentResponse storeDocument(StoreDocumentRequest request) {
		StoreDocumentResponse response = new StoreDocumentResponse();
		Result result = new Result();
		result.setResultCode(-99);
		result.setResultDescription("Unknown Error");
		response.setResult(result);
		
		if(request.getDocument().getId()==null) {
			result.setResultCode(-1);
			result.setResultDescription("ID is required");
			return response;
		}
		
		if(getDocumentDAO().checkIfExists(request.getDocument().getId())) {
			result.setResultCode(-2);
			result.setResultDescription("ID already exists: " + request.getDocument().getId());
			return response;
		}
		
		
		try {
			DocumentContent content = new DocumentContent();
			content.setId(request.getDocument().getId());
			content.setContent(request.getDocument().getContent());

			TransactionBody<String> txnBody = new TransactionBody<String>() {
			    public String execute() {
			    	
					try {
//						crkidServices.getDokumentStatusService().storeItem(status);
						String hdcId = UUID.randomUUID().toString();

						
						RepositoryDocument doc = getRepositoryDocument(request, content);
						
						
						if(request.getDocument().getOperation()!=null && request.getDocument().getOperation().isTrackChanges()) {
							
							String hId = UUID.randomUUID().toString();
							doc.setHistoryContentId(hdcId);
							HistoryRepositoryDocument hDoc = new HistoryRepositoryDocument(doc, hId);
							hDoc.setChangedContent(true);
//							BasicDBObject rdoc = getDocumentDAO().getDBObject(doc.getId());
//							String hdcId = rdoc.getString("historyContentId");
//							rdoc.put("_id", hId);
//							rdoc.put("documentId", doc.getId());
//							rdoc.put("historyContentId", hdcId);
							DocumentContent docContent = content;
							HistoryDocumentContent hdContent = new HistoryDocumentContent();
							hdContent.setContent(docContent.getContent());
							hdContent.setDocumentId(request.getDocument().getId());
							hdContent.setDocumentVersion(1);
							hdContent.setId(hdcId);
							hDokumentContentDAO.storeItem(hdContent);
//							hDocumentDAO.storeDBObject(rdoc);
							hDocumentDAO.storeItem(hDoc);
						} 
						
						dokumentContentDAO.storeItem(content);
						getDocumentDAO().storeItem(doc);
						
						if(request.getDocument().getOperation()!=null && request.getDocument().getOperation().isTrackChanges()) {
							getDocumentDAO().updateObjectProperty(request.getDocument().getId(), "changedContent", false);
						} else {
							getDocumentDAO().updateObjectProperty(request.getDocument().getId(), "changedContent", true);
						}
						
					} catch (DuplicateKeyException ex) {
						throw new DuplicateKeyRuntimeException(ex);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}				    	
			    	
			        return "done";
			    }
			};
			
			getDocumentDAO().withTransaction(txnBody);
			result.setResultCode(1);
			result.setResultDescription("OK");
			
			RepositoryDocument rd = getDocumentDAO().getObject(request.getDocument().getId());
//			response.setMetadata(rd.getMetadata());
//			response.setOperation(rd.getOperation());
//			response.setId(rd.getId());
//			response.setVersion(rd.getVersion());
//			response.setCreated(rd.getCreated());
//			response.setModified(rd.getModified());	
			response.setDocument(repositoryDocumentToDocument(rd));
			

		} catch (DuplicateKeyRuntimeException e) {
//			logException(e);
			result.setResultCode(-5);
			result.setResultDescription(e.getMessage());
		} catch (Exception e) {
			logException(e);
			result.setResultCode(-99);
			result.setResultDescription(e.getMessage());
		}		
		
		return response;
	}

	@Override
	public GetDocumentResponse getDocument(GetDocumentRequest request) {
		GetDocumentResponse response = new GetDocumentResponse();
		Result result = new Result();
		result.setResultCode(-99);
		result.setResultDescription("Unknown Error");
		response.setResult(result);
		
		if(request.getId()==null) {
			if(request.getDbId() != null) {
				return getDocumentByDbId(request.getDbId());
			} else {
				result.setResultCode(-1);
				result.setResultDescription("ID is required");
				return response;
			}
		}
		
		if(!getDocumentDAO().checkIfExists(request.getId())) {
			result.setResultCode(-2);
			result.setResultDescription("There is no document with ID: " + request.getId());
			return response;
		}

		try {
			RepositoryDocument rd = getDocumentDAO().getObject(request.getId());
			Document doc = repositoryDocumentToDocument(rd);
			DocumentContent content = dokumentContentDAO.getObject(request.getId());
			doc.setContent(content.getContent());
			response.setDocument(doc);
			result.setResultCode(1);
			result.setResultDescription("OK");
		} catch (IOException e) {
			logException(e);
			result.setResultCode(-99);
			result.setResultDescription(e.getMessage());
		}
		return response;
	}

	private GetDocumentResponse getDocumentByDbId(Long dbId) {
		GetDocumentResponse response = new GetDocumentResponse();
		Result result = new Result();
		result.setResultCode(-99);
		result.setResultDescription("Unknown Error");
		response.setResult(result);
		try {
			RepositoryDocument document = getDocumentDAO().getByDbId(dbId);
			if (document != null) {
				Document doc = repositoryDocumentToDocument(document);
				DocumentContent content = dokumentContentDAO.getObject(document.getId());
				doc.setContent(content.getContent());
				response.setDocument(doc);
				result.setResultCode(1);
				result.setResultDescription("OK");
			} else {
				result.setResultCode(-2);
				result.setResultDescription("There is no document with ID: " + dbId);
				return response;
			}
		} catch (IOException e) {
			logException(e);
			result.setResultCode(-99);
			result.setResultDescription(e.getMessage());
		}
		return response;
	}

	@Override
	public UpdateDocumentResponse updateDocument(UpdateDocumentRequest request) {
		UpdateDocumentResponse response = new UpdateDocumentResponse();
		Result result = new Result();
		result.setResultCode(-99);
		result.setResultDescription("Unknown Error");
		response.setResult(result);
		
		if(request.getId()==null) {
			result.setResultCode(-1);
			result.setResultDescription("ID is required");
			return response;
		}
		
		if(!getDocumentDAO().checkIfExists(request.getId())) {
			result.setResultCode(-2);
			result.setResultDescription("There is no document with ID: " + request.getId());
			return response;
		}
		if(!getDocumentDAO().checkIfExists(request.getId(), request.getVersion())) {
			result.setResultCode(-3);
			result.setResultDescription("Document version is not " + request.getVersion());
			return response;
		}
		
		try {
			
			byte[] nContent = request.getContent();
			//TODO: sprawdzanie content size i MD5, ignorowanie identycznego contentu


			TransactionBody<String> txnBody = new TransactionBody<String>() {
			    public String execute() {
			    	
					try {
						getDocumentDAO().updateVersion(request.getId(), request.getVersion());
						if(nContent!=null) {
							DocumentContent content = new DocumentContent();
							content.setId(request.getId());
							content.setContent(request.getContent());
							dokumentContentDAO.replaceObject(content);
							getDocumentDAO().updateObjectProperty(request.getId(), "changedContent", true);
						} else {
//							getDocumentDAO().updateObjectProperty(request.getId(), "changedContent", false);
						}
						getDocumentDAO().updateDocument(request);
						
						if(request.getOperation()!=null && request.getOperation().isTrackChanges()) {
							
							String hId = UUID.randomUUID().toString();
							HistoryDocumentContent historyDocumentContent = null;
							RepositoryDocument doc = getDocumentDAO().getObject(request.getId());
							HistoryRepositoryDocument hDoc = new HistoryRepositoryDocument(doc, hId);
							String hdcId = hDoc.getHistoryContentId();
//							doc.put("_id", hId);
//							doc.put("documentId", request.getId());							
							
							
							if(hdcId==null) {
								hdcId = UUID.randomUUID().toString();
								hDoc.setHistoryContentId(hdcId);
//								doc.put("historyContentId", hdcId);
								DocumentContent docContent = dokumentContentDAO.getObject(request.getId());
								HistoryDocumentContent hdContent = new HistoryDocumentContent();
								hdContent.setContent(docContent.getContent());
								hdContent.setDocumentId(request.getId());
								hdContent.setDocumentVersion(request.getVersion());
								hdContent.setId(hdcId);
								hDokumentContentDAO.storeItem(hdContent);
								if(request.getContent()==null) {
									getDocumentDAO().updateObjectProperty(request.getId(), "historyContentId", hdcId);
								}
							}
							hDocumentDAO.storeItem(hDoc);
							
							getDocumentDAO().updateObjectProperty(request.getId(), "changedContent", false);
							
						}						
						
					} catch (Exception e) {
						throw new RuntimeException(e);
					}				    	
			    	
			        return "done";
			    }
			};
			
			getDocumentDAO().withTransaction(txnBody);
			
			RepositoryDocument rd = getDocumentDAO().getObject(request.getId());
//			response.setMetadata(rd.getMetadata());
//			response.setOperation(rd.getOperation());
//			response.setId(rd.getId());
//			response.setVersion(rd.getVersion());
//			response.setCreated(rd.getCreated());
//			response.setModified(rd.getModified());			
			response.setDocument(repositoryDocumentToDocument(rd));
			
			result.setResultCode(1);
			result.setResultDescription("OK");


		} catch (Exception e) {
			logException(e);
			result.setResultCode(-99);
			result.setResultDescription(e.getMessage());
		}
		
		
		return response;
	}
	
	@Override
	public DeleteDocumentResponse deleteDocument(DeleteDocumentRequest request) {
		DeleteDocumentResponse response = new DeleteDocumentResponse();
		Result result = new Result();
		result.setResultCode(-99);
		result.setResultDescription("Unknown Error");
		response.setResult(result);
		
		if(request.getId()==null) {
			result.setResultCode(-1);
			result.setResultDescription("ID is required");
			return response;
		}
		
		if(!getDocumentDAO().checkIfExists(request.getId())) {
			result.setResultCode(-2);
			result.setResultDescription("There is no document with ID: " + request.getId());
			return response;
		}
		
		try {
			
			TransactionBody<String> txnBody = new TransactionBody<String>() {
			    public String execute() {
			    	
					try {


						Query query = new Query();
						query = query.addCriteria(where("documentId").is(request.getId()));
						query = query.with(Sort.by(Sort.Direction.DESC, "version"));
//						List<HistoryRepositoryDocument> sResult = hDocumentDAO.find(filter, 0, 0, sort);
						List<HistoryRepositoryDocument> sResult = hDocumentDAO.find(query);
						if(sResult.size()>0) {
							RepositoryDocument rd = sResult.get(0);
							DocumentContent content = hDokumentContentDAO.getObject(rd.getHistoryContentId());
							dokumentContentDAO.replaceObject(content, true);
							getDocumentDAO().replaceObject(rd, true);							
						} else {
							dokumentContentDAO.deleteObject(request.getId());
							getDocumentDAO().deleteObject(request.getId());
						}
						
					} catch (Exception e) {
						throw new RuntimeException(e);
					}				    	
			    	
			        return "done";
			    }
			};
			
			getDocumentDAO().withTransaction(txnBody);
			
			RepositoryDocument rd = getDocumentDAO().getObject(request.getId());
//			response.setMetadata(rd.getMetadata());
//			response.setOperation(rd.getOperation());
//			response.setId(rd.getId());
//			response.setVersion(rd.getVersion());
//			response.setCreated(rd.getCreated());
//			response.setModified(rd.getModified());
			if(rd != null) {
				response.setDocument(repositoryDocumentToDocument(rd));
			}

			
			result.setResultCode(1);
			result.setResultDescription("OK");
		} catch (Exception e) {
			logException(e);
			result.setResultCode(-99);
			result.setResultDescription(e.getMessage());
		}
		
		
		return response;
	}

	@Override
	public FindDocumentResponse findDocument(FindDocumentRequest request) {
		FindDocumentResponse response = new FindDocumentResponse();
		Result result = new Result();
		result.setResultCode(-99);
		result.setResultDescription("Unknown Error");
		response.setResult(result);
		List<Bson> filters = new ArrayList<>();
		List<Document> documents = new ArrayList<>();
		
		try {
			
//			Metadata md = request.getMetadata();
//			if(md!=null) {
//				if(md.getContentType()!=null) {
//					Bson mdf = Filters.eq("metadata.contentType", md.getContentType());
//					filters.add(mdf);
//				}
//				if(md.getLocalReferenceNumber()!=null) {
//					Bson mdf = Filters.eq("metadata.localReferenceNumber", md.getLocalReferenceNumber());
//					filters.add(mdf);
//				}
//				if(md.getCustomsReferenceNumber()!=null) {
//					Bson mdf = Filters.eq("metadata.customsReferenceNumber", md.getCustomsReferenceNumber());
//					filters.add(mdf);
//				}
//				if(md.getDate()!=null) {
//					Bson mdf = Filters.eq("metadata.date", md.getDate());
//					filters.add(mdf);
//				}
//			}
//			
//			if(request.getMetadata()!=null && request.getMetadata().getOtherMetadata() != null) {
//				for(OtherMetadata omd : request.getMetadata().getOtherMetadata()) {
//					Bson omdFilter = Filters.elemMatch("metadata.otherMetadata", Filters.and(Filters.eq("name", omd.getName()),Filters.eq("value", omd.getValue())));
//					filters.add(omdFilter);
//				}
//			}		
//			
//			Bson sort = new BasicDBObject("created", -1);
//			Bson filter = Filters.and(filters);
			List<RepositoryDocument> sResult = getDocumentDAO().find(request);
			
			for(RepositoryDocument rd : sResult) {
				Document doc = new Document();
				doc.setMetadata(rd.getMetadata());
				doc.setId(rd.getId());
				doc.setVersion(rd.getVersion());
				doc.setCreated(rd.getCreated());
				doc.setModified(rd.getModified());
	
	
				documents.add(doc);
			}
			response.setDocuments(documents);
			
		} catch (Exception e) {
			logException(e);
			result.setResultCode(-99);
			result.setResultDescription(e.getMessage());
		}
		return response;
	}

	@Override
	public GetDocumentHistoryResponse getDocumentHistory(GetDocumentHistoryRequest request) {
		GetDocumentHistoryResponse response = new GetDocumentHistoryResponse();
		Result result = new Result();
		result.setResultCode(-99);
		result.setResultDescription("Unknown Error");
		response.setResult(result);
		
		if(request.getId()==null) {
			result.setResultCode(-1);
			result.setResultDescription("ID is required");
			return response;
		}

		
		try {
			
			List<DocumentHistory> documents = new ArrayList<>();
			
			
//			Bson sort = new BasicDBObject("version", -1);
//			Bson filter = Filters.eq("documentId",request.getId());
//
//			if(request.getVersion()!=null && request.getVersion()!=0) {
//				filter = Filters.and(Filters.eq("documentId",request.getId()), Filters.eq("version",request.getVersion()));
//			}
//			if(request.getOperationName()!=null) {
//				filter = Filters.and(filter, Filters.in("operation.name",request.getOperationName()));
//			}
			
	//		List<HistoryRepositoryDocument> sResult = hDocumentDAO.find(filter, 0, 0, sort);
			
			Criteria criteria = where("documentId").is(request.getId());
			if(request.getVersion()!=null && request.getVersion()!=0) {
				criteria = criteria.and("version").is(request.getVersion());
			}
			if(request.getOperationName()!=null) {
				criteria = criteria.and("operation.name").in(request.getOperationName());
			}
			
			Query query = new Query();
			query = query.addCriteria(criteria);
			query = query.with(Sort.by(Sort.Direction.DESC, "version"));
			List<HistoryRepositoryDocument> sResult = hDocumentDAO.find(query);			
			
			
			
			for(HistoryRepositoryDocument rd : sResult) {
				DocumentHistory doc = new DocumentHistory();
				doc.setMetadata(rd.getMetadata());
				doc.setOperation(rd.getOperation());
				doc.setId(rd.getId());
				doc.setVersion(rd.getVersion());
				doc.setCreated(rd.getCreated());
				doc.setModified(rd.getModified());
				doc.setChangedContent(rd.isChangedContent());
				
				if(request.isWithContent() && rd.isChangedContent()) {
					DocumentContent content = hDokumentContentDAO.getObject(rd.getHistoryContentId());
					doc.setContent(content.getContent());
				}
				documents.add(doc);
				
				if(request.isLatest()) {
					break;
				}
			}
			
			

			response.setDocuments(documents);
			result.setResultCode(1);
			result.setResultDescription("OK");
		} catch (IOException e) {
			logException(e);
			result.setResultCode(-99);
			result.setResultDescription(e.getMessage());
		}
		
		
		return response;
	}
	
	protected void logException(Exception e){
		log.error(ExceptionUtils.getStackTrace(e));
	}

	@Override
	public GetDocumentVersionResponse getDocumentVersion(GetDocumentVersionRequest request) {
		GetDocumentVersionResponse response = new GetDocumentVersionResponse();
		Result result = new Result();
		result.setResultCode(-99);
		result.setResultDescription("Unknown Error");
		response.setResult(result);
		
		if(request.getId()==null) {
			result.setResultCode(-1);
			result.setResultDescription("ID is required");
			return response;
		}
		
		if(!getDocumentDAO().checkIfExists(request.getId())) {
			result.setResultCode(-2);
			result.setResultDescription("There is no document with ID: " + request.getId());
			return response;
		}
		
		try {
			RepositoryDocument rd = getDocumentDAO().getObject(request.getId());
			response.setVersion(rd.getVersion());
			result.setResultCode(1);
			result.setResultDescription("OK");
		} catch (IOException e) {
			logException(e);
			result.setResultCode(-99);
			result.setResultDescription(e.getMessage());
		}
		
		
		return response;
	}
	
	protected String getMD5(byte[] content) {
		return DigestUtils.md5Hex(content);
	}
	
	protected void repositoryDocumentToDocument(RepositoryDocument rd, Document doc) {
		doc.setMetadata(rd.getMetadata());
		doc.setOperation(rd.getOperation());
		doc.setId(rd.getId());
		doc.setDbId(rd.getDbId());
		doc.setVersion(rd.getVersion());
		doc.setCreated(rd.getCreated());
		doc.setModified(rd.getModified());
	}

	protected Document repositoryDocumentToDocument(RepositoryDocument rd) {
		Document doc = new Document();
		repositoryDocumentToDocument(rd, doc);
		return doc;
	}

	protected DocumentDAO getDocumentDAO() {
		return documentDAO;
	}

	protected RepositoryDocument getRepositoryDocument(StoreDocumentRequest request, DocumentContent content) {
		RepositoryDocument doc = new RepositoryDocument();
		doc.setMetadata(request.getDocument().getMetadata());
		doc.setOperation(request.getDocument().getOperation());
		doc.setCreated(LocalDateTime.now());
		doc.setId(request.getDocument().getId());
		doc.setDbId(request.getDocument().getDbId());
		doc.setVersion(1);
		doc.setContentSize(content.getContent().length);
		doc.setContentMD5(getMD5(content.getContent()));
		return doc;
	}
	
}
