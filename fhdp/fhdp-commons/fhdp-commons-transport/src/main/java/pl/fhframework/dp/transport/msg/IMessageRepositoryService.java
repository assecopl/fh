package pl.fhframework.dp.transport.msg;

import pl.fhframework.dp.transport.drs.repository.*;
import pl.fhframework.dp.transport.drs.repository.*;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 15/10/2020
 */
public interface IMessageRepositoryService {
    Document getDocument(String repositoryId);

    boolean isDocumentExist(String id);

    StoreDocumentResponse storeDocument(StoreDocumentRequest request);
    
    UpdateDocumentResponse updateDocument(UpdateDocumentRequest request);
    
    
}
