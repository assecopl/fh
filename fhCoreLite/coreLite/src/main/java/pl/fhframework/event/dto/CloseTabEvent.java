package pl.fhframework.event.dto;

import lombok.Getter;

@Getter
public class CloseTabEvent extends EventDTO {
    private String uuid;

    public CloseTabEvent(String uuid) {
        this.uuid = uuid;
    }
}
