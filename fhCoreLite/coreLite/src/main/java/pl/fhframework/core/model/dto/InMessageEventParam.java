package pl.fhframework.core.model.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * Container for any message being sent to server.
 * Part of InMessageEventData (events from 'browser')
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@Data
public abstract class InMessageEventParam {
    private final String type;

    public InMessageEventParam(String type) {
        this.type = type;
    }
}
