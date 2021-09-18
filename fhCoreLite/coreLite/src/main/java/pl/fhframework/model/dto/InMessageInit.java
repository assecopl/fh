package pl.fhframework.model.dto;

import lombok.Data;
import pl.fhframework.Commands;

/**
 * Server input message - clients initial message
 */
@Data
public class InMessageInit extends AbstractMessage {

    private String url;

    public InMessageInit() {
        super(Commands.IN_INIT);
    }
}
