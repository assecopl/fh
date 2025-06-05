package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class InputDateOptimizedElement extends ComponentElement {
    private LocalDate inputDateRequired = LocalDate.now();
    private LocalDate inputDateRequiredFormatExample = LocalDate.now();
    private LocalDate inputDateOnChange = LocalDate.of(2016, 12, 31);
    private LocalDate inputDateValidationRule = LocalDate.of(2017, 4, 25);
    private LocalDate inputDateBeforeValidationRule = LocalDate.of(2017, 4, 26);
}
