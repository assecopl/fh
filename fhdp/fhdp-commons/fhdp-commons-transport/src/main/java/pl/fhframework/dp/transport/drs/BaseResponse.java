package pl.fhframework.dp.transport.drs;

import pl.fhframework.dp.transport.drs.Result;

public class BaseResponse {
	protected pl.fhframework.dp.transport.drs.Result result = new pl.fhframework.dp.transport.drs.Result();

	public pl.fhframework.dp.transport.drs.Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
