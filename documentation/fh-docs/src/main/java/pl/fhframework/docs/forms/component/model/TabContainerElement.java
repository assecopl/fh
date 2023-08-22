package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TabContainerElement extends ComponentElement {
    private Integer boundActiveTabIndex = 1;
    private String boundActiveTabId = "boundTabOneId";
    private List<Integer> radioToActiveTabBinding = Arrays.asList(new Integer(0), new Integer(1));
}
