package pl.fhframework.core.model.dto.client;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * Container for any message being sent to server.
 * Part of InClientData message proccessed by handlers
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@Data
public abstract class AbstractClientMessage {
    private String serviceId;

    private String type;

    public AbstractClientMessage(String type) {
        this.type = type;
    }
}
