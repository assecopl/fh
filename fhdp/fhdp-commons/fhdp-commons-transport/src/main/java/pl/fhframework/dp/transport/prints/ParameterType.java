
package pl.fhframework.dp.transport.prints;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class ParameterType
    implements Serializable
{

    private final static long serialVersionUID = 1L;

    protected String name;
    protected String value;
    protected String type;

}
