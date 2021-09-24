package pl.fhframework.core.model.dto.client;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Container for any message being sent to client.
 * Part of DataToClientEvent
 */

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@AllArgsConstructor
public abstract class AbstractClientOutputData {
    private String serviceId;
}
