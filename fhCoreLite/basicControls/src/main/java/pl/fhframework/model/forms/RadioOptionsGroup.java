package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerControl;
import pl.fhframework.annotations.DocumentedComponent;
import pl.fhframework.annotations.TemplateControl;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

/**
 * RadioOptionsGroup is container grouping RadioOption components.
 * It is used to add common logic to options group eg. reaction to 'on change' events.
 */
@TemplateControl(tagName = "fh-radio-options-group")
@DesignerControl(defaultWidth = 3)
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, documentationExample = true, value = "RadioOptionsGroup component aggregating single radio components", icon = "fa fa-ellipsis-v", ignoreFields = {"emptyValue", "emptyLabel"})
public class RadioOptionsGroup extends BaseInputListField {

    public RadioOptionsGroup(Form form) {
        super(form);
    }

    @Override
    public RadioOptionsGroup createNewSameComponent() {
        return new RadioOptionsGroup(getForm());
    }

}
