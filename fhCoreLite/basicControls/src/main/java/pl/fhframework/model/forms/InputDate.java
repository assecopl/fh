package pl.fhframework.model.forms;

import pl.fhframework.core.FhBindingException;
import pl.fhframework.annotations.*;

import java.time.LocalDate;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.SPECIFIC;

/**
 * Class represents input field for date. Receives every attribute of input field. This field can be
 * used inside PanelGroup, Column, Tab, Form.
 */
@DocumentedComponent(value = "Component responsible for displaying field, where user can set only date.", icon = "fa fa-calendar")
@Control(parents = {PanelGroup.class, Column.class, Tab.class, Row.class, Form.class, Group.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = LocalDate.class, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))
public class InputDate extends BaseInputFieldWithKeySupport {

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Date format, may be one of following described here: http://momentjs.com/docs/#/displaying/format/")
    private String format;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Should mask characters be added just in time typing.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 96)
    private Boolean maskDynamic;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Should today be highlighted in datepicker.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 96)
    private Boolean highlightToday;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "false")
    @DocumentedComponentAttribute(value = "Creates interactive mask for user")
    private boolean maskEnabled;

    public InputDate(Form container) {
        super(container);
    }

    @Override
    public InputDate createNewSameComponent() {
        return new InputDate(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, BaseInputField baseClone) {
        super.doCopy(table, iteratorReplacements, baseClone);
        InputDate clone = (InputDate) baseClone;
        clone.setMaskDynamic(getMaskDynamic());
        clone.setHighlightToday(getHighlightToday());
        clone.setFormat(getFormat());
    }

    @Override
    protected void processCoversionException(FhBindingException cfe) {
        // here do nothing, but ovveride areValueTheSame
    }

    @Override
    protected boolean areModelValuesTheSame(Object firstValue, Object secondValue) {
        return !this.isValidConversion() || super.areValuesTheSame(firstValue, secondValue);
    }
}
