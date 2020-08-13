package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

@TemplateControl(tagName = "fh-input-number")
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, documentationExample = true, ignoreFields = {"icon", "iconAlignment"},
        value = "Component responsible for displaying field, where use can set only number.", icon = "fa fa-edit")
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = Number.class, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))
public class InputNumber extends BaseInputFieldWithKeySupport {


    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(priority = 121, functionalArea = SPECIFIC)
    @DocumentedComponentAttribute(value = "Defines how many fraction digits can be used.")
    private Integer maxFractionDigits;

    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(priority = 122, functionalArea = SPECIFIC)
    @DocumentedComponentAttribute(value = "Defines how many integer digits can be used.")
    private Integer maxIntigerDigits;

    public InputNumber(Form form) {
        super(form);
    }

    @Override
    public InputNumber createNewSameComponent() {
        return new InputNumber(getForm());
    }

}
