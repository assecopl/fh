package pl.fhframework.docs.forms.component.model;


import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.designer.ComponentElement;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SectionElement extends ComponentElement {
}
