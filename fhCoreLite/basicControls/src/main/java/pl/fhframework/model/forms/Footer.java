package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;

@Control(parents = {PanelGroup.class, Table.class}, canBeDesigned = true)
public class Footer extends GroupingComponent<Component> {
    public Footer(Form form) {
        super(form);
    }


}
