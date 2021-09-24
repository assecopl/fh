package pl.fhframework.core.model.dto.client.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import pl.fhframework.core.model.dto.InMessageEventParam;
import pl.fhframework.core.model.dto.ModuleSubTypesDeserializer;
import pl.fhframework.core.model.dto.client.AbstractClientMessage;
import pl.fhframework.core.model.dto.client.ClientMessageType;

import java.util.Map;

/**
 * Map parameter to events
 */
@Data
@JsonSubTypes(
        @JsonSubTypes.Type(value = InMessageMapParam.class, name = ClientMessageType.IN_CLIENT_MAP_PARAM))
public class InMessageMapParam extends InMessageEventParam {
    private Map<String, Object> attributes;

    public InMessageMapParam() {
        super(ClientMessageType.IN_CLIENT_MAP_PARAM);
    }
}
