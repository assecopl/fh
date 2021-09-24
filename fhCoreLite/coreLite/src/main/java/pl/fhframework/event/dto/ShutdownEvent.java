package pl.fhframework.event.dto;

import lombok.Getter;

/**
 * An event sent to client when server is shutting down
 */
@Getter
public class ShutdownEvent extends EventDTO {

    private boolean graceful;

    public ShutdownEvent() {
    }

    public ShutdownEvent(boolean graceful) {
        this.graceful = graceful;
    }
}
