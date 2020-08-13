package pl.fhframework.core.model.dto.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import pl.fhframework.core.model.dto.ModuleSubTypesDeserializer;
import pl.fhframework.Commands;
import pl.fhframework.model.dto.AbstractMessage;

/**
 * Server input message - messages from JS Client
 */
@Data
public class InClientData extends AbstractMessage {
    @JsonDeserialize(using = ModuleSubTypesDeserializer.class)
    private AbstractClientMessage clientMessage;

    public InClientData() {
        super(Commands.IN_CLIENT_DATA);
    }
}
