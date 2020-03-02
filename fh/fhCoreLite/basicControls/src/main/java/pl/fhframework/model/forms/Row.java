package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.model.forms.attribute.ElementsHorizontalAlign;
import pl.fhframework.model.forms.attribute.ElementsVerticalAlign;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.LOOK_AND_STYLE;

/**
 * Row component is responsible for placing components in one row
 * <p>
 * Example: {@code <Row></Row>}
 */

@OverridenPropertyAnnotations(
        designerXmlProperty = @DesignerXMLProperty(skip = true),
        property = "width"
)
@DesignerControl
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Form.class, Repeater.class, Group.class, SplitContainer.class}, invalidParents = {Row.class, Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.ARRANGEMENT, value = "Row component is responsible for placing components in one row", icon = "fa fa-bars")
public class Row extends GroupingComponent<Component> {
    public Row(Form form) {
        super(form);
    }

    /**
     * Horizontal-align of the child components
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 75)
    @DocumentedComponentAttribute(defaultValue = "LEFT", value = "Horizontal align of the child components")
    private ElementsHorizontalAlign elementsHorizontalAlign;

    /**
     * Vertical-align of the child components
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 74)
    @DocumentedComponentAttribute(value = "Vertical-align of the child components")
    private ElementsVerticalAlign elementsVerticalAlign;
}
