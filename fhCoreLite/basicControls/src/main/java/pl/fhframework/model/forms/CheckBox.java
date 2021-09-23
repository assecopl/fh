package pl.fhframework.model.forms;

import pl.fhframework.annotations.*;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;
import pl.fhframework.model.forms.validation.ValidationFactory;
import pl.fhframework.validation.ValidationManager;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

@TemplateControl(tagName = "fh-input-checkbox")
@DesignerControl(defaultWidth = 2)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, documentationExample = true, value = "CheckBox is component which let a user select MANY of a limited number of choices. It is" +
        " displayed as HTML input type = checkbox element.", icon = "fa fa-check-square")
@Control(parents = {PanelGroup.class, Group.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = Boolean.class, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))
public class CheckBox extends BaseInputField {

    public CheckBox(Form form) {
        super(form);
    }

    @Override
    public CheckBox createNewSameComponent() {
        return new CheckBox(getForm());
    }

    @Override
    protected ValidationManager<BaseInputField> createValidationManager() {
        return ValidationFactory.getInstance().getCheckBoxValidationProcess();
    }
}
