package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.CompiledBinding;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * Class represents input field for date and time. Receives every attribute of input field.  This
 * field can be used inside PanelGroup, Column, Tab, Form.
 */
@TemplateControl(tagName = "fh-input-timestamp")
@DesignerControl(defaultWidth = 3)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, value = "Component responsible for displaying field, where use can set date and time.", icon = "fa fa-clock")
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = {Date.class,LocalDateTime.class}, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))
public class InputTimestamp extends BaseInputFieldWithKeySupport {

    //Formater fo deprecated java.util.Date
    private static final String DEFAULT_DATE_TIME_FORMATTER = "defaultDateTimeFormatter";
    private static final String OPTIONAL_LOCAL_DATE_TIME_FORMATTER = "optionalLocalDateTimeFormatter";

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Date format, may be one of following described here: http://momentjs.com/docs/#/displaying/format/")
    private String format;

    public InputTimestamp(Form form) {
        super(form);
    }

    @Override
    public InputTimestamp createNewSameComponent() {
        return new InputTimestamp(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, BaseInputField baseClone) {
        super.doCopy(table, iteratorReplacements, baseClone);
        InputTimestamp clone = (InputTimestamp) baseClone;
        clone.setFormat(getFormat());
    }


    @Override
    protected void processCoversionException(FhBindingException cfe) {
        // here do nothing, but ovveride areValueTheSame
    }

    @JsonIgnore
    @Override
    public Optional<String> getOptionalFormatter() {
        ModelBinding m = this.getModelBinding();
        BindingResult b = m.getBindingResult();
        if (m instanceof CompiledBinding) {
            if(((CompiledBinding) m).getTargetType() == Date.class){
                return Optional.of(DEFAULT_DATE_TIME_FORMATTER);
            }
        }

        return Optional.of(OPTIONAL_LOCAL_DATE_TIME_FORMATTER);
    }

    @Override
    protected boolean areModelValuesTheSame(Object firstValue, Object secondValue) {
        return !this.isValidConversion() || super.areValuesTheSame(firstValue, secondValue);
    }
}
