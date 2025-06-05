package pl.fhframework.docs.forms.component.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.designer.ComponentElement;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AnchorElement extends ComponentElement {

    private Boolean scrollRightNow;
    private Boolean scrollRightNowInside = false;
    private Boolean scrollRightNowInside2 = false;
    private Boolean scrollRightNowInside3 = false;
    private Integer duration;
}
