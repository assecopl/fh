package pl.fhframework.event.dto;

import lombok.Getter;

@Getter
public class CustomActionEvent extends EventDTO {

    private String actionName;

    private String data;

    public CustomActionEvent(String actionName) {
        this.actionName = actionName;
    }

    public CustomActionEvent(String actionName, String data) {
        this.actionName = actionName;
        this.data = data;
    }
}
