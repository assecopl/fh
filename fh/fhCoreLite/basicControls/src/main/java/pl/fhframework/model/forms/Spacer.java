package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerControl;
import pl.fhframework.annotations.DocumentedComponent;

/**
 * Class representing xml component of Spacer. Every field represents xml attribute of spacer tag.
 * Example {@code <Spacer size="value_1"/>}. Every field is parsed as json for javascript. If field
 * should be ingored by JSON, use <code>@JsonIgnore</code>. There can be used any annotations for
 * json generator.
 */
@DesignerControl(defaultWidth = 2)
@Control(parents = {PanelGroup.class, Group.class, Repeater.class, Column.class, Tab.class, Row.class, Form.class, Footer.class, Dropdown.class}, canBeDesigned = true, invalidParents = {Table.class})
@DocumentedComponent(category = DocumentedComponent.Category.ARRANGEMENT, value = "Component used to create spaces in a layout", icon = "fas fa-arrows-alt-h")
public class Spacer extends FormElement implements Invisible {

    public Spacer(Form form) {
        super(form);
    }
    
}
