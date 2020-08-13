package pl.fhframework.event.dto;

import lombok.Getter;

@Getter
public class StylesheetChangeEvent extends EventDTO {
    private String name;

    public StylesheetChangeEvent(String name) {
        this.name = name;
    }
}
