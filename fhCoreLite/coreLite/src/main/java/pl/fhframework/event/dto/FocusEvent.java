package pl.fhframework.event.dto;

import lombok.Getter;

@Getter
public class FocusEvent extends EventDTO {
    private String containerId;
    private String formElementId;

    public FocusEvent(String containerId, String formElementId) {
        this.formElementId = formElementId;
        this.containerId = containerId;
    }
}
