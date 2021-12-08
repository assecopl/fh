package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.BaseRequest;

@Getter @Setter
public class CancelProcessRequest extends BaseRequest {
	String processExecutionId;
	Long declarationId;
	String declarationLrn;
}
