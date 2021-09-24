package pl.fhframework.model.dto;

import pl.fhframework.Commands;

/**
 * Server input message - request for session metadata
 */
public class InMessageGetSessionMetadata extends AbstractMessage {

    public InMessageGetSessionMetadata() {
        super(Commands.IN_GET_SESSION_ID);
    }
}
