package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;

@Control(parents = {Dashboard.class}, canBeDesigned = true)
public class AvailableWidgets extends GroupingComponent<Component> {

    public AvailableWidgets(Form form) {
        super(form);
    }

}
