package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;

@Control(parents = {Dashboard.class}, canBeDesigned = true)
public class DefaultWidgets extends GroupingComponent<Component> {

    public DefaultWidgets(Form form) {
        super(form);
    }

}
