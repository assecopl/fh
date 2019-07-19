package pl.fhframework.model.dto.cloud;

import lombok.Data;
import pl.fhframework.Commands;
import pl.fhframework.model.dto.AbstractMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * Message to a cloud server containig use case management informations.
 */
@Data
public class InMessageCloudUseCaseManagement extends AbstractMessage implements ICloudMessage {

    private boolean clearUseCaseStack = true;

    public InMessageCloudUseCaseManagement() {
        super(Commands.IN_CLOUD_USE_CASE_MANAGEMENT);
    }
}
