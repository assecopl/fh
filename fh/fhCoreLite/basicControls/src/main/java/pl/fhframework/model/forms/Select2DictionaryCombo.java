package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DocumentedComponent;

@DocumentedComponent(documentationExample = true, value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
        icon = "fa fa-outdent")
@Control(parents = {PanelGroup.class, Group.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = false)
public class Select2DictionaryCombo extends DictionaryCombo {
    public Select2DictionaryCombo(Form<?> form) {
        super(form);
    }

    protected void processFiltering(String text) {
        filteredObjectValues.clear();
        filteredObjectValues.putAll(values);
        filterInvoked = true;
    }
}




