package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;

/**
 * To be removed in near future!!
 */
@Control(parents = {Tree.class})
@Deprecated
public class Node extends GroupingComponent<FormElement> {

    private static final String ATTR_LABEL = "label";

    @Getter
    @Setter
    @XMLProperty(value = ATTR_LABEL)
    @DocumentedComponentAttribute(value = "Node label")
    private String label;

    public Node(Form form) {
        super(form);
    }
}
