package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.BaseResponse;

@Getter @Setter
public class GetDocumentVersionResponse extends BaseResponse {
	int version;
}
