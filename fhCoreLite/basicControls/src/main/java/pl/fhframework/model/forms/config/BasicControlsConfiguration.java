package pl.fhframework.model.forms.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.fhframework.model.forms.attribute.HintType;

import javax.annotation.PostConstruct;

@Component
public class BasicControlsConfiguration {
    private static BasicControlsConfiguration INSTANCE;
    @Value("${fh.web.inputText.maxLength:65535}")
    @Getter
    private Integer inputTextMaxLength;


    @Value("${fh.web.tablePaged.pageSizeAsButtons:false}")
    @Getter
    private Boolean tablePagedPageSizeAsButtons;

    @Value("${fh.web.tablePaged.pageSizes:5,10,15,25}")
    @Getter
    private String tablePagedPageSizes;

    @Value("${fh.web.formElement.hintTpe:STANDARD}")
    @Getter
    private HintType formElementHintType;

    @Value("${fh.web.secure.caprcha.sitekey:null}")
    @Getter
    private String captchaSiteKey;

    @Value("${fh.web.secure.caprcha.serverkey:null}")
    @Getter
    private String captchaServerKey;

    @Autowired
    @Getter
    private RestTemplate restTemplate;

    @PostConstruct
    void init () {
        INSTANCE = this;
    }

    public static BasicControlsConfiguration getInstance() {
        return INSTANCE;
    }
}
