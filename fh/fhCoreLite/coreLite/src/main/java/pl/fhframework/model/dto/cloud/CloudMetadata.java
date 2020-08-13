package pl.fhframework.model.dto.cloud;

import lombok.Data;

/**
 * Metadata being sent between servers in a cloud.
 */
@Data
public class CloudMetadata {

    private String userSessionId;

    private CloudPropagatedSession sessionToCreate;

    private Boolean nonExistingSession;
}
