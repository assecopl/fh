package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.BaseResponse;
import pl.fhframework.dp.transport.drs.repository.Document;

@Getter @Setter
public class DeleteDocumentResponse extends BaseResponse {
	protected Document document;
}
