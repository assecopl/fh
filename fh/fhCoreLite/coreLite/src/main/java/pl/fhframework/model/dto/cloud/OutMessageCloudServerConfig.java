package pl.fhframework.model.dto.cloud;

import lombok.Data;
import pl.fhframework.core.cloud.config.CloudServerDefinition;
import pl.fhframework.Commands;
import pl.fhframework.model.dto.AbstractMessage;

/**
 * Message from a cloud server containig it's configuration.
 */
@Data
public class OutMessageCloudServerConfig extends AbstractMessage implements ICloudMessage {

    private CloudServerDefinition serverFunctionalDefinition = new CloudServerDefinition();
    private int cloudServerDefinitionId;
    private boolean changed = true;

    public OutMessageCloudServerConfig() {
        super(Commands.OUT_CLOUD_CONFIG);
    }
}
