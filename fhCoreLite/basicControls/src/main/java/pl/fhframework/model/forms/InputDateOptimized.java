package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponent;
import pl.fhframework.annotations.OverridenPropertyAnnotations;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.time.LocalDate;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * Class represents input field for date. Receives every attribute of input field. This field can be
 * used inside PanelGroup, Column, Tab, Form.
 */
@DocumentedComponent(documentationExample = true, value = "Component responsible for displaying field, where user can set only date. Optimized for IE", icon = "fa fa-calendar")
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Group.class, Repeater.class, ColumnOptimized.class}, invalidParents = {Table.class}, canBeDesigned = false)
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = LocalDate.class, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))
public class InputDateOptimized extends InputDate {
    public InputDateOptimized(Form container) {
        super(container);
    }
}
