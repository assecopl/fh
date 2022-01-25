package pl.fhframework.dp.transport.service;

import pl.fhframework.dp.transport.drs.repository.*;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 10/12/2021
 */
public interface IRepositoryService {
    StoreDocumentResponse storeDocument(StoreDocumentRequest request);

    GetDocumentResponse getDocument(GetDocumentRequest request);

    UpdateDocumentResponse updateDocument(UpdateDocumentRequest request);

    DeleteDocumentResponse deleteDocument(DeleteDocumentRequest request);

    FindDocumentResponse findDocument(FindDocumentRequest request);

    GetDocumentHistoryResponse getDocumentHistory(GetDocumentHistoryRequest request);

    GetDocumentVersionResponse getDocumentVersion(GetDocumentVersionRequest request);
}
