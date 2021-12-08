package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.BaseResponse;

@Getter @Setter
public class DeleteDocumentResponse extends BaseResponse {
	protected Document document;
}
