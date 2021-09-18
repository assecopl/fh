package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DocumentedComponent;
import pl.fhframework.events.IEventSource;

/**
 * RadioOptionsGroup is container grouping RadioOption components.
 * It is used to add common logic to options group eg. reaction to 'on change' events.
 */
@Control(parents = {PanelGroup.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(value = "RadioOptionsGroup component aggregating single radio components", icon = "fa fa-ellipsis-v", ignoreFields = {"emptyValue", "emptyLabel"})
public class RadioOptionsGroup extends BaseInputListField {

    public RadioOptionsGroup(Form form) {
        super(form);
    }

    @Override
    public RadioOptionsGroup createNewSameComponent() {
        return new RadioOptionsGroup(getForm());
    }

}
