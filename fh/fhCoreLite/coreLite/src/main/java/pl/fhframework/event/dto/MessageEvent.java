package pl.fhframework.event.dto;

import lombok.Getter;

@Getter
public class MessageEvent extends EventDTO {

    private String title;

    private String message;

    public MessageEvent(String title, String message) {
        this.title = title;
        this.message = message;
    }
}
