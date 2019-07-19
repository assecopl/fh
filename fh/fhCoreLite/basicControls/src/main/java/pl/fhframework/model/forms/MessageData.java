package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageData {

    private String elementId;
    private String elementLabel;
    private String message;

    public MessageData(String elementId, String message) {
        this.elementId = elementId;
        this.message = message;
    }

    public MessageData(String elementId, String elementLabel, String message) {
        this.elementId = elementId;
        this.elementLabel = elementLabel;
        this.message = message;
    }
}
