package pl.fhframework.event.dto;

import lombok.Getter;
import pl.fhframework.Session;

@Getter
public class SessionTimeoutEvent extends EventDTO {
    private Session.TimeoutData data;

    private SessionTimeoutEvent() {
    }

    public SessionTimeoutEvent(Session.TimeoutData data) {
        this.data = data;
    }
}
