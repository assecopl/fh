package pl.fhframework.dp.transport.drs;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Result {
	Integer resultCode;
	String resultDescription;

    public Result() {
    }

    public Result(int code, String msg) {
        this.resultCode = code;
        this.resultDescription = msg;
    }
}
