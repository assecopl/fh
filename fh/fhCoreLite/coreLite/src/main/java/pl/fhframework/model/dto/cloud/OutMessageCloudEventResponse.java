package pl.fhframework.model.dto.cloud;

import lombok.Data;
import pl.fhframework.Commands;
import pl.fhframework.events.ExternalCallbackInvocation;
import pl.fhframework.model.dto.AbstractMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Message from a cloud server containig response on event. Encapsulates raw response to be sent to client.
 */
@Data
public class OutMessageCloudEventResponse extends AbstractMessage implements ICloudMessage {

    private String serializedClientPartialResponse;

    private List<CloudFormInfo> currentForms = new ArrayList<>();

    private List<String> propagatedExternalResponses = new ArrayList<>();

    private ExternalCallbackInvocation propagatedCallbackInvocation;

    public OutMessageCloudEventResponse() {
        super(Commands.OUT_CLOUD_EVENT_RESPONSE);
    }
}
