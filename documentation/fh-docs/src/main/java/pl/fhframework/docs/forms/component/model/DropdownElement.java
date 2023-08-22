package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by k.czajkowski on 19.01.2017.
 */
@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DropdownElement extends ComponentElement {

    public static final String INITIAL = "Initial Label";
    public static final String CHANGED_LABEL = "Changed Label";

    private String dropdownLabel = INITIAL;
    private String initialLabel = INITIAL;
    private String changedLabel = CHANGED_LABEL;
    private boolean initialValue;
}
