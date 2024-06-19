package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by k.czajkowski on 05.01.2017.
 */
@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InputNumberElement extends ComponentElement {

    private String onChangeValue = "";
    private String requiredValue = "32";
    private Integer intAttr;
    private BigDecimal bigDecAttr;
    private Double doubleAttr;
}
