package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.convert.ConversionFailedException;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;
import pl.fhframework.validation.IValidationResults;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.SPECIFIC;

/**
 * Class represents input field for date. Receives every attribute of input field. This field can be
 * used inside PanelGroup, Column, Tab, Form.
 */
@TemplateControl(tagName = "fh-input-date")
@DesignerControl(defaultWidth = 3)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, documentationExample = true, value = "Component responsible for displaying field, where user can set only date.", icon = "fa fa-calendar")
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Group.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = {LocalDate.class, String.class}, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))
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

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Is date valid.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 96)
    private ModelBinding<Boolean> invalidDate;


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


    //    @Override
    protected void processCoversionException(FhBindingException cfe) {}

    protected void processInvalidDateInformation(){
        if(invalidDate != null) {
            this.updateBindingForValue(!this.isValidConversion(), invalidDate, invalidDate.getBindingExpression(), Optional.empty());
        }
    }

    @Override
    protected boolean areModelValuesTheSame(Object firstValue, Object secondValue) {
        return !this.isValidConversion() || super.areValuesTheSame(firstValue, secondValue);
    }


    @Override
    public void updateModel(ValueChange valueChange) {
       super.updateModel(valueChange);
        processInvalidDateInformation();
    }


    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();

        return elementChanges;
    }

    @Override
    protected boolean processValueBinding(ElementChanges elementChanges) {
        boolean hasChange = super.processValueBinding(elementChanges);
        if(hasChange) {
            this.validConversion = true;
            this.updateBindingForValue(!this.isValidConversion(), invalidDate, invalidDate.getBindingExpression(), Optional.empty());

        }
        return hasChange;
    }



}
