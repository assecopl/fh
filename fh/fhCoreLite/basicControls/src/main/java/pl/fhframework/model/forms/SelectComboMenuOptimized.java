package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DocumentedComponent;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

@DocumentedComponent(documentationExample = true, value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering. Optimized for IE",
        icon = "fa fa-outdent")
@Control(parents = {PanelGroup.class, Group.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class, ColumnOptimized.class}, invalidParents = {Table.class}, canBeDesigned = false)
public class SelectComboMenuOptimized extends SelectComboMenu {
    public SelectComboMenuOptimized(Form form) {
        super(form);
    }
}
