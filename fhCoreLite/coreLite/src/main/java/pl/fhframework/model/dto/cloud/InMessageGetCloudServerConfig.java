package pl.fhframework.model.dto.cloud;

import pl.fhframework.Commands;
import pl.fhframework.model.dto.AbstractMessage;

/**
 * Cloud server input message - request for cloud server config
 */
public class InMessageGetCloudServerConfig extends AbstractMessage implements ICloudMessage {

    private int knownCloudServerDefinitionId;

    public InMessageGetCloudServerConfig() {
        super(Commands.IN_GET_CLOUD_CONFIG);
    }

    public int getKnownCloudServerDefinitionId() {
        return knownCloudServerDefinitionId;
    }

    public void setKnownCloudServerDefinitionId(int knownCloudServerDefinitionId) {
        this.knownCloudServerDefinitionId = knownCloudServerDefinitionId;
    }
}
