package pl.fhframework.dp.transport.drs;

public class BaseResponse {
	protected Result result = new Result();

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
