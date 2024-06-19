package pl.fhframework.docs.forms.component.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.designer.ComponentElement;

import java.time.LocalDate;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Select2DictionaryComboElement extends ComponentElement {

    LocalDate dataReferencyjna = LocalDate.now();
    Object startValue;
    Object secondValue;
    String codeListId = "007";

}
