package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.BaseResponse;

import java.util.List;

public class GetDocumentHistoryResponse extends BaseResponse {
	@Getter @Setter
	protected List<DocumentHistory> documents;
}
