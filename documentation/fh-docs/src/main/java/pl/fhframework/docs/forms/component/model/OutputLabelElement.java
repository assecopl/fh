package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.core.designer.ComponentElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OutputLabelElement extends ComponentElement {
    private Person samplePerson = new Person(10l, "John", "Gadbery", "San Fransisco", "Male", "Active", null, null, null, null);
    private boolean displayLink;
    private String buttonLabel = "Show link";
}
