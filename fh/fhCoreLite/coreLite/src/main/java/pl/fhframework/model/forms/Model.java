package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.XMLProperty;

import java.util.List;

/**
 * Defines form's internal model.
 */
@Control(parents = Form.class)
public class Model extends GroupingComponent<Property> {

    public Model(Form form) {
        super(form);
    }

    @Getter
    @Setter
    @XMLProperty
    private String externalClass;

    public List<Property> getProperties() {
        return getSubcomponents();
    }
}