package pl.fhframework.model.dto;

import lombok.Data;
import pl.fhframework.Commands;

/**
 * Server input message - sent upon URL change
 */
@Data
public class InMessageUrlChange extends AbstractMessage {

    private String url;

    public InMessageUrlChange() {
        super(Commands.IN_URL_CHANGE);
    }
}
