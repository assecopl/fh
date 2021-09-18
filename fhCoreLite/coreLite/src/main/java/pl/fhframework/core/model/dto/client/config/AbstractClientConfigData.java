package pl.fhframework.core.model.dto.client.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * Container for any message being sent from / to server.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@Data
public abstract class AbstractClientConfigData {

    private final String type;

    public AbstractClientConfigData(String type) {
        this.type = type;
    }
}
