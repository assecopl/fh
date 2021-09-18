package pl.fhframework.model.dto;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.Commands;

/**
 * Message from a server containig user session information.
 */
@Getter
@Setter
public class OutMessageSessionMetadata extends AbstractMessage {

    private String sessionId;

    public OutMessageSessionMetadata(String sessionId) {
        this();
        this.sessionId = sessionId;
    }

    public OutMessageSessionMetadata() {
        super(Commands.OUT_CONNECTION_ID);
    }
}
