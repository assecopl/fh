package pl.fhframework.event.dto;

import lombok.Getter;

@Getter
public class LanguageChangeEvent extends EventDTO {
    private String code;

    public LanguageChangeEvent(String code) {
        this.code = code;
    }
}
