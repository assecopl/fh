package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponent;
import pl.fhframework.annotations.OverridenPropertyAnnotations;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, ignoreFields = {"icon", "iconAlignment"},
        value = "Component responsible for displaying field, where use can set only number.", icon = "fa fa-edit")
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = Number.class, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))
public class InputNumber extends BaseInputFieldWithKeySupport {

    public InputNumber(Form form) {
        super(form);
    }

    @Override
    public InputNumber createNewSameComponent() {
        return new InputNumber(getForm());
    }

}
