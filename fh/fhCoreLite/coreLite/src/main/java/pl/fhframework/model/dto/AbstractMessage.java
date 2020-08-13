package pl.fhframework.model.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import pl.fhframework.Commands;
import pl.fhframework.core.model.dto.client.InClientData;
import pl.fhframework.model.dto.cloud.*;

/**
 * Container for any message being sent from / to server.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "command")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InMessageEventData.class, name = Commands.IN_HANDLE_EVENT),
        @JsonSubTypes.Type(value = InMessageGetSessionMetadata.class, name = Commands.IN_GET_SESSION_ID),
        @JsonSubTypes.Type(value = InMessageInit.class, name = Commands.IN_INIT),
        @JsonSubTypes.Type(value = InMessageGetCloudServerConfig.class, name = Commands.IN_GET_CLOUD_CONFIG),
        @JsonSubTypes.Type(value = InMessageRunUseCase.class, name = Commands.IN_RUN_USE_CASE),
        @JsonSubTypes.Type(value = InMessageRunCloudUseCase.class, name = Commands.IN_CLOUD_RUN_USE_CASE),
        @JsonSubTypes.Type(value = InMessageUrlChange.class, name = Commands.IN_URL_CHANGE),
        @JsonSubTypes.Type(value = OutMessageCloudServerConfig.class, name = Commands.OUT_CLOUD_CONFIG),
        @JsonSubTypes.Type(value = OutMessageEventHandlingResult.class, name = Commands.OUT_SET),
        @JsonSubTypes.Type(value = OutMessageSessionMetadata.class, name = Commands.OUT_CONNECTION_ID),
        @JsonSubTypes.Type(value = InMessageCloudUseCaseManagement.class, name = Commands.IN_CLOUD_USE_CASE_MANAGEMENT),
        @JsonSubTypes.Type(value = OutMessageCloudEventResponse.class, name = Commands.OUT_CLOUD_EVENT_RESPONSE),
        @JsonSubTypes.Type(value = InClientData.class, name = Commands.IN_CLIENT_DATA)
})
@Data
public abstract class AbstractMessage {

    private final String command;

    private CloudMetadata cloudMetadata;

    public AbstractMessage(String command) {
        this.command = command;
    }
}
