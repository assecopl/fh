package pl.fhframework.model.forms.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BasicControlsConfiguration {
    private static BasicControlsConfiguration INSTANCE;
    @Value("${fh.web.inputText.maxLength:65535}")
    @Getter
    private Integer inputTextMaxLength;

    @PostConstruct
    void init () {
        INSTANCE = this;
    }

    public static BasicControlsConfiguration getInstance() {
        return INSTANCE;
    }
}
