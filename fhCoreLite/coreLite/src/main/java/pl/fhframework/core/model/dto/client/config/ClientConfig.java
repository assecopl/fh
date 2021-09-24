package pl.fhframework.core.model.dto.client.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import pl.fhframework.core.model.dto.client.AbstractClientMessage;
import pl.fhframework.core.model.dto.client.ClientMessageType;
import pl.fhframework.core.model.dto.ModuleSubTypesDeserializer;

/**
 * Server input message - clients initial message
 */
@Data
@JsonSubTypes(
        @JsonSubTypes.Type(value = ClientConfig.class, name = ClientMessageType.IN_CLIENT_CONFIG))
public class ClientConfig extends AbstractClientMessage {
    @JsonDeserialize(using = ModuleSubTypesDeserializer.class)
    private AbstractClientConfigData clientConfigData;

    public ClientConfig() {
        super(ClientMessageType.IN_CLIENT_CONFIG);
    }
}
