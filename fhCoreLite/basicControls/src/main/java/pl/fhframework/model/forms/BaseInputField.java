package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.convert.ConversionFailedException;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.core.forms.IValidatedComponent;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.attribute.IconAlignment;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.model.LabelPosition;
import pl.fhframework.model.forms.validation.ValidationFactory;
import pl.fhframework.validation.*;

import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.*;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

/**
 * Base component for input fields (like CheckBox, InputText etc.)
 */
@DesignerControl(defaultWidth = 2)
public abstract class BaseInputField extends FormElementWithConfirmationSupport implements IChangeableByClient, Iconable, Boundable, IValidatedComponent, TableComponent<BaseInputField>, IHasBoundableLabel {

    protected static final String ON_CHANGE_ATTR = "onChange";
    protected static final String RAW_VALUE_ATTR = "rawValue";
    protected static final String VALUE_ATTR = "value";
    protected static final String REQUIRED_ATTR = "required";
    protected static final String LABEL_ATTR = "label";
    protected static final String VALIDATION_LABEL_ATTR = "validationLabel";
    protected static final String PRESENTATION_STYLE_ATTR = "presentationStyle";
    protected static final String MESSAGE_FOR_FIELD_ATTR = "messageForField";
    protected static final String AUTOCOMPLETE_ATTR = "autocomplete";
    protected static final String HINT_INPUT_GROUP_ATTR = "hintInputGroup";


    /**
     * Placement of the hint for component
     */
    @Getter
    @Setter
    @XMLProperty(value = HINT_INPUT_GROUP_ATTR)
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 86)
    @DocumentedComponentAttribute(defaultValue = "STANDARD", value = "Static presentation makes hint appears after clikc on '?' icon that will appear as input group element.")
    private Boolean hintInputGroup = false;

    @Getter
    @Setter
    private String rawValue = "";

    @JsonIgnore
    @Getter
    @Setter
    @TwoWayBinding
    @XMLProperty(value = VALUE_ATTR, aliases = Combo.SELECTED_ITEM_ATTR)
    // when changing @DesignerXMLProperty also change in @OverridenPropertyAnnotations on InputNumber,CheckBox,InputDate,InputText,InputTimestamp
    @DesignerXMLProperty(commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Binding represents value from model of Form, used inside of '{}', like {model}.")
    private ModelBinding modelBinding;

    @Getter
    @XMLProperty
    @DocumentedComponentAttribute(value = "If there is some value, representing method in use case, then on every action in input, " +
            "that method will be executed. This method fires, when component loses focus.")
    @DesignerXMLProperty(priority = 100, functionalArea = BEHAVIOR)
    private ActionBinding onChange;

    @Getter
    private String label = "";   // requires default value for designer (for now). Otherwise there are some errors, while displaying component with JS.

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = LABEL_ATTR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, priority = 110, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Represents label for created component. Supports FHML - FH Markup Language.")
    private ModelBinding labelModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = VALIDATION_LABEL_ATTR)
    @DesignerXMLProperty(previewValueProvider = BindingExpressionDesignerPreviewProvider.class, priority = 100, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Represents label for created component used in validation messages. If not set, falls back to label attribute's value.")
    private ModelBinding validationLabelModelBinding;

    @Getter
    @Setter
    private PresentationStyleEnum presentationStyle;

    @Getter
    private String messageForField = "";

    @Getter
    @Setter
    @JsonIgnore
    @XMLProperty
    @DocumentedComponentAttribute(value = "User can define validation rule for binded model using SpEL. Expression is based on properties of form's model and must be prefixed with '-' sign, eg. -prop1 < prop2")
    @DesignerXMLProperty(priority = 90)
    private String validationRule;

    @Getter
    private boolean required;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(REQUIRED_ATTR)
    @DesignerXMLProperty(priority = 5, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, defaultValue = "false", value = "User can define if component is required for Form. Binding changes may not be respected after initially showing this control.")
    private ModelBinding<Boolean> requiredBinding;

    @Getter
    private String icon;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(ICON)
    @DocumentedComponentAttribute(boundable = true, value = "Icon id. Please refer to http://fontawesome.io/icons/ for all available icons.")
    @DesignerXMLProperty(priority = 84, functionalArea = LOOK_AND_STYLE)
    private ModelBinding<String> iconBinding;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "BEFORE")
    @DesignerXMLProperty(priority = 83, functionalArea = LOOK_AND_STYLE)
    @DocumentedComponentAttribute(defaultValue = "BEFORE", boundable = true, value = "Icon alignment - possible values are before or after. Final alignment depends of component where this attribute is used.")
    private IconAlignment iconAlignment = IconAlignment.BEFORE;

    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 95)
    @DocumentedComponentAttribute(value = "Defines position of a label. Position is one of: up, down, left, right.")
    private LabelPosition labelPosition;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "60.0")
    @DocumentedComponentAttribute(defaultValue = "60.0", value = "Proportional size of input, inputSize should be set when labelPosition is \"left\" or \"right\".")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 91, allowedTypes = Double.class)
    private double inputSize = 60.0;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "")
    @DocumentedComponentAttribute(defaultValue = "", value = "Proportional size of label, labelSize should be set when labelPosition is \"left\" or \"right\". If this value is set, then inputSize property does not work.")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 96, allowedTypes = String.class)
    private String labelSize = null;

    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 94)
    @DocumentedComponentAttribute(value = "Defines label control id.")
    private String labelId;

    @JsonIgnore
    protected ValidationManager<BaseInputField> validationManager;

    @JsonIgnore
    @Getter
    protected boolean validConversion = true;

    /*
        WCAG 2.1 1.3.5 implementation
        Values : https://www.w3.org/TR/html52/sec-forms.html#autofill-detail-tokens
    */
    @Getter
    private String autocomplete = null;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = AUTOCOMPLETE_ATTR)
    @DesignerXMLProperty(commonUse = true, priority = 110, functionalArea = BEHAVIOR)
    @DocumentedComponentAttribute(boundable = true, value = "Represents 'autocomplete' attribute. Help browsers suggest right content. More at : https://www.w3.org/TR/html52/sec-forms.html#autofill-detail-tokens")
    private ModelBinding autocompleteModelBinding;



    public BaseInputField(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        validationManager = createValidationManager();
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        //TODO: We should skip this action if component is not editable
        this.rawValue = StringUtils.emptyToNull(valueChange.getMainValue()); //Just in case we set it, but method which refreshes bindings will PROBABLY change it later. But with this line we will not have unnecessary traffic to client which does not do anything and client will not lose invalid value
        if (modelBinding != null) {
            try {
                this.updateBindingForValue(this.rawValue, modelBinding, modelBinding.getBindingExpression(), this.getOptionalFormatter());
                this.validConversion = true;
            } catch (FhBindingException cfe) {
                if (cfe.getCause() instanceof ConversionFailedException || cfe.getCause() instanceof ParseException || cfe.getCause() instanceof DateTimeParseException) {
                    this.validConversion = false;
                    processCoversionException(cfe);
                }
                else {
                    throw cfe;
                }
            }
        }
    }

    protected void processCoversionException(FhBindingException cfe) {
        throw cfe;
    }


    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ON_CHANGE_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onChange);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        if (iconBinding != null) {
            icon = resolveIconBinding(this, elementChanges);
        }
        if (requiredBinding != null) {
            required = requiredBinding.resolveValueAndAddChanges(this, elementChanges, required, REQUIRED_ATTR);
        }
        boolean refreshView = processValueBinding(elementChanges);
        refreshView |= processLabelBinding(elementChanges);
        refreshView |= processAutocompleteBinding(elementChanges);
        this.prepareComponentAfterValidation(elementChanges);
        if (refreshView) {
            refreshView();
        }
        return elementChanges;
    }

    protected String convertToRaw(BindingResult<?> bindingResult) {
        if (bindingResult == null) {
            return "";
        }
        Optional<String> converterName = this.getOptionalFormatter();
        return this.convertBindingValueToString(bindingResult, converterName);
    }

    protected boolean processValueBinding(ElementChanges elementChanges) {
        BindingResult bindingResult = modelBinding != null ? modelBinding.getBindingResult() : null;
        skipSettingPresentation(elementChanges, getForm());
        String newRawValue = convertToRaw(bindingResult);

        if (!areModelValuesTheSame(newRawValue, rawValue)) {
            this.rawValue = newRawValue;
            elementChanges.addChange(RAW_VALUE_ATTR, this.rawValue);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void calculateAvailability() {
        super.calculateAvailability();
        checkBinding();
    }

    protected void checkBinding() {
        if (getAvailability() == null || getAvailability() == AccessibilityEnum.EDIT) {
            if (modelBinding != null && !modelBinding.canChange()) {
                setAvailability(AccessibilityEnum.VIEW);
            }
        }
    }

    @Deprecated
    public void setModelBindingAdHoc(String binding) {
        setModelBinding(createAdHocModelBinding(binding));
    }

    @Deprecated
    public void setLabelModelBindingAdHoc(String labelBinding) {
        setLabelModelBinding(createAdHocModelBinding(labelBinding));
    }

    protected boolean processLabelBinding(ElementChanges elementChanges) {
        BindingResult labelBidingResult = labelModelBinding != null ? labelModelBinding.getBindingResult() : null;
        String newLabelValue = labelBidingResult == null ? null : this.convertBindingValueToString(labelBidingResult);
        if (!areValuesTheSame(newLabelValue, label)) {
            this.label = newLabelValue;
            elementChanges.addChange(LABEL_ATTR, this.label);
            return true;
        }
        return false;
    }

    protected boolean processAutocompleteBinding(ElementChanges elementChanges) {
        BindingResult bidingResult = autocompleteModelBinding != null ? autocompleteModelBinding.getBindingResult() : null;
        String newValue = bidingResult == null ? null : this.convertBindingValueToString(bidingResult);
        if (!areValuesTheSame(newValue, autocomplete)) {
            this.autocomplete = newValue;
            elementChanges.addChange(AUTOCOMPLETE_ATTR, this.autocomplete);
            return true;
        }
        return false;
    }

    @Override
    public void validate() {
        if (this.modelBinding != null && this.modelBinding.getBindingResult() != null) {
            processValidationForThisComponent();
        }
    }

    @Override
    public void prepareComponentAfterValidation(ElementChanges elementChanges) {
        if (getForm().getAbstractUseCase() != null) {
            IValidationResults validationResults = getForm().getAbstractUseCase().getUserSession().getValidationResults();

            BindingResult bindingResult = this.getTargetModelBinding() != null ? this.getTargetModelBinding().getBindingResult() : null;
            List<FieldValidationResult> fieldValidationResultFor = bindingResult == null ? Collections.emptyList() : validationResults.getFieldValidationResultFor(bindingResult.getParent(), bindingResult.getAttributeName());
            if (getAvailability() != AccessibilityEnum.EDIT) {
                fieldValidationResultFor.removeIf(FieldValidationResult::isFormSource);
            }
            processStylesAndHints(elementChanges, fieldValidationResultFor);
        }
    }

    @JsonIgnore
    @Override
    public List<ModelBinding> getAllBingings() {
        List<ModelBinding> allBindings = new ArrayList<>();
        allBindings.add(getModelBinding());
        allBindings.add(getLabelModelBinding());
        return allBindings;
    }

    protected List<ConstraintViolation<BaseInputField>> processValidationForThisComponent() {
        List<ConstraintViolation<BaseInputField>> formComponentValidationResult = validationManager.validate(this);
        IValidationResults validationResults = getForm().getAbstractUseCase().getUserSession().getValidationResults();
        BindingResult bindingResult = this.getTargetModelBinding().getBindingResult();

        for (ConstraintViolation<BaseInputField> constrain : formComponentValidationResult) {
            validationResults.addCustomMessageForComponent(this, bindingResult.getParent(), bindingResult.getAttributeName(), constrain.getMessage(), PresentationStyleEnum.BLOCKER);
        }

        return formComponentValidationResult;
    }

    protected ValidationManager<BaseInputField> createValidationManager() {
        return ValidationFactory.getInstance().getBasicValidationProcess();
    }

    protected void processStylesAndHints(ElementChanges elementChanges, List<FieldValidationResult> fieldValidationResults) {
        // styling
        FormFieldHints formFieldHints = processPresentationStyle(elementChanges, fieldValidationResults);
        // messaging
        processMessagesForHints(elementChanges, formFieldHints);
    }

    private void processMessagesForHints(ElementChanges elementChanges, FormFieldHints formFieldHints) {
        String oldHints = this.messageForField;
        this.messageForField = formFieldHints != null ? formFieldHints.getHints() : null;
        if (!Objects.equals(oldHints, messageForField)) {
            elementChanges.addChange(MESSAGE_FOR_FIELD_ATTR, this.messageForField);
        }
    }

    protected FormFieldHints processPresentationStyle(ElementChanges elementChanges, List<FieldValidationResult> fieldValidationResults) {
        PresentationStyleEnum oldPresentationStyle = this.presentationStyle;
        FormFieldHints formFieldHints = null;
        BindingResult bindingResult = getTargetModelBinding() != null ? getTargetModelBinding().getBindingResult() : null;
        if (bindingResult != null) {
            formFieldHints = calculatePresentationStyle(getTargetModelBinding().getBindingResult());
            this.presentationStyle = (formFieldHints != null) ? formFieldHints.getPresentationStyleEnum() : null;
        } else {
            this.presentationStyle = null;
        }
        if (!fieldValidationResults.isEmpty() && (this.presentationStyle == null || this.presentationStyle != PresentationStyleEnum.BLOCKER)) {
            this.presentationStyle = PresentationStyleEnum.BLOCKER;
        }
        if (oldPresentationStyle != this.presentationStyle) {
            elementChanges.addChange(PRESENTATION_STYLE_ATTR, this.presentationStyle);
        }
        return formFieldHints;
    }

    protected ModelBinding getTargetModelBinding() {
        return getModelBinding();
    }

    protected FormFieldHints calculatePresentationStyle(BindingResult bindingResult) {
        if (bindingResult.getParent() != null && bindingResult.getAttributeName() != null && getAvailability() != AccessibilityEnum.HIDDEN) {
            FormFieldHints fieldHints = getForm().getAbstractUseCase().getUserSession().getValidationResults().getPresentationStyleForField(bindingResult.getParent(), bindingResult.getAttributeName());
            if (fieldHints == null) {
                fieldHints = getForm().getFieldsHighlightingList().getPresentationStyleForField(bindingResult.getParent(), bindingResult.getAttributeName());
            }
            return fieldHints;
        } else {
            return null;
        }
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, BaseInputField inputFieldClone) {
        TableComponent.super.doCopy(table, iteratorReplacements, inputFieldClone);
        inputFieldClone.setModelBinding(table.getRowBinding(getModelBinding(), inputFieldClone, iteratorReplacements));
        inputFieldClone.setOnChange(table.getRowBinding(getOnChange(), inputFieldClone, iteratorReplacements));
        inputFieldClone.setLabelModelBinding(table.getRowBinding(getLabelModelBinding(), inputFieldClone, iteratorReplacements));
        inputFieldClone.setValidationRule(table.getRowBinding(getValidationRule(), iteratorReplacements, false));
        inputFieldClone.setRequiredBinding(table.getRowBinding(getRequiredBinding(), inputFieldClone, iteratorReplacements));
        inputFieldClone.setIconBinding(table.getRowBinding(getIconBinding(), inputFieldClone, iteratorReplacements));
        inputFieldClone.setLabelPosition(getLabelPosition());
        inputFieldClone.setInputSize(getInputSize());
        inputFieldClone.setLabelSize(getLabelSize());
        inputFieldClone.setHintInputGroup(getHintInputGroup());
        inputFieldClone.setAutocompleteModelBinding(table.getRowBinding(getAutocompleteModelBinding(), inputFieldClone, iteratorReplacements));
        inputFieldClone.setAriaLabelBinding(table.getRowBinding(this.getAriaLabelBinding(), inputFieldClone, iteratorReplacements));

    }

    protected String convertMainValueToString(Object value, Optional<String> converterName) {
        return this.convertValueToString(value, converterName.orElse(null));
    }

    public void setOnChange(ActionBinding onChange) {
        this.onChange = onChange;
    }

    public IActionCallbackContext setOnChange(IActionCallback onChange) {
        return CallbackActionBinding.createAndSet(onChange, this::setOnChange);
    }

}

