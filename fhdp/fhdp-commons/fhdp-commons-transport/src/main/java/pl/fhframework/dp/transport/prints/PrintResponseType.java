
package pl.fhframework.dp.transport.prints;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PrintResponseType {

    protected ResultType result;
    protected List<PrintoutType> printout;

}
