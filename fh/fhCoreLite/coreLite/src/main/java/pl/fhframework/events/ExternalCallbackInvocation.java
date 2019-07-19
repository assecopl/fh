package pl.fhframework.events;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Piotr on 2017-09-14.
 */
@Data
@AllArgsConstructor
public class ExternalCallbackInvocation {

    private String methodName;

    private String[] paramJsons;
}
