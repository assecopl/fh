package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InputTextElement extends ComponentElement {
    private String requiredValue = "Remove this value and click on any other InputText";
    private String onChangeValue = "";
    private String onInputValue = "";
    private Date inputTextConverterExample;
    private String xml = "";
    private String maskedField = "";
    private String regex = "";
    private String regexMasked = "";
    private String highlight = "";
}
