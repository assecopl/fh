package pl.fhframework.docs.forms.component.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.core.designer.DocumentedAttribute;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PanelGroupWrapperElement extends ComponentElement {
    private int activeGroup = 1;
    public Boolean toggleAll = true;
    private DocumentedAttribute attr;
    public DocumentedAttribute transform(DocumentedAttribute attr) {
        return attr;
    }
    public DocumentedAttribute join(DocumentedAttribute attr, DocumentedAttribute attr2) {
        return attr;
    }

}
