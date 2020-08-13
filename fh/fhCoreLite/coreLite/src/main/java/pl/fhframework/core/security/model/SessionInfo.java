/**
 * *********************************************************
 * Author: krzysztof.kozlowski2
 * Created: 2018-03-15
 * **********************************************************
 */
package pl.fhframework.core.security.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

@Getter
@Setter
@EqualsAndHashCode
public class SessionInfo implements Serializable {

    private String sessionId;
    private String userName;
    private Date logonTime;
    private String nodeUrl;
    private Function<SessionInfo, String> activeUseCaseFunction;

    /** Returns user active functionality */
    public String getUserActiveFunctionality() {
        if (activeUseCaseFunction != null) {
            return activeUseCaseFunction.apply(this);
        } else {
            return null;
        }
    }

}
