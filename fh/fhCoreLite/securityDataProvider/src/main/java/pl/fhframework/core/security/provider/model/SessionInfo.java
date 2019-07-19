/**
 * *********************************************************
 * Author: krzysztof.kozlowski2
 * Created: 2018-03-15
 * **********************************************************
 */
package pl.fhframework.core.security.provider.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class SessionInfo {

    private String sessionId;
    private String userName;
    private Date logonTime;
    private String usedFunctionality;

}
