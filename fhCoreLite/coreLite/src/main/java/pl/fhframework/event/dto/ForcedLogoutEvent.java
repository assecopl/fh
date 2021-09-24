/**
 * *********************************************************
 * Author: krzysztof.kozlowski2
 * Created: 2018-05-08
 * **********************************************************
 */
package pl.fhframework.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An event sent to client when their session was terminated by admin.
 */
@Getter
public class ForcedLogoutEvent extends EventDTO {

    @AllArgsConstructor
    public enum Reason {

        LOGOUT_FORCE("force"),

        LOGOUT_TIMEOUT("timeout");

        @Getter
        private String code;
    }

    private String reason;

    public ForcedLogoutEvent(Reason reason) {
        this.reason = reason.getCode();
    }
}
