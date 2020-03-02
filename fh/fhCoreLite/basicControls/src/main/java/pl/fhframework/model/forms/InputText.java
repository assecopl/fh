package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.TextAlignEnum;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.forms.config.BasicControlsConfiguration;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.designer.RegExAttributeDesignerSupport;
import pl.fhframework.model.forms.optimized.ColumnOptimized;
import pl.fhframework.model.forms.utils.LanguageResolver;
import pl.fhframework.model.forms.validation.ValidationFactory;
import pl.fhframework.tools.loading.IBodyXml;
import pl.fhframework.validation.ValidationManager;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

@DocumentedComponent(value = "InputText component is responsible for displaying simple field, where user can write some data" +
        " plus label representing this field.", icon = "fa fa-edit")
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = String.class, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))
public class InputText extends BaseInputFieldWithKeySupport implements IBodyXml {
    // attributes as string
    private static final String ON_INPUT_ATTR = "onInput";
    private static final String MASK_BINDING = "maskBinding";
    private static final String MASK = "mask";
    private static final String MAX_LENGTH_BINDING = "maxLengthBinding";
    private static final String FORMATTER_BINDING = "formatterBinding";
    private static final String REQUIRED_REGEX_BINDING = "requiredRegexBinding";

    // basic attributes
    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "If there is some value, then there will be visible placeholder for <input>.")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 82)
    private String placeholder;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "If there will be value greater then 0, then InputText component will be represented " +
            "as html tag: <textarea></textarea>. Otherwise, simple <input/>.")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 93)
    private Integer rowsCount;

    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 92)
    @DocumentedComponentAttribute(value = "Alignment of text inside input.")
    private TextAlignEnum textAlign;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "65535")
    @DocumentedComponentAttribute(value = "Maximum length of value")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 90)
    private Integer maxLength;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = MAX_LENGTH_BINDING)
    @DocumentedComponentAttribute(boundable = true, value = "Maximum length of value")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 81)
    private ModelBinding maxLengthBinding;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Formatter mask")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 96)
    private String mask;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Should mask characters be added just in time typing.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 96)
    private Boolean maskDynamic;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Can mask characters be overriden.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 96)
    private Boolean maskInsertMode;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Custom symbol to use in mask. First character is always validator, second part defines allowed characters (between square brackets). Multiple values are separated with double pipe (\"||\". For example: x[123]||d[1-8].")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 95)
    private String maskDefinition;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = MASK_BINDING)
    @DocumentedComponentAttribute(boundable = true, value = "Formatter mask.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 94)
    private ModelBinding maskBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Regular expression expected to be matched by input value (if present)")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 50, support = RegExAttributeDesignerSupport.class)
    private String requiredRegex;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = REQUIRED_REGEX_BINDING)
    @DocumentedComponentAttribute(boundable = true, value = "Regular expression expected to be matched by input value (if present)")
    @DesignerXMLProperty(functionalArea = SPECIFIC)
    private ModelBinding requiredRegexBinding;

    @JsonIgnore
    @Getter
    private Pattern requiredRegexPattern;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(boundable = true, value = "Validation message show if requiredRegex expression is not matched by input value (if present)")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 49)
    private ModelBinding<String> requiredRegexMessage;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Type - text, password")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 92)
    private String inputType;

    // actions
    @Getter
    @XMLProperty
    @DocumentedComponentAttribute(value = "If there is some value, representing method in use case, then on every action in input, " +
            " that method will be executed. Action is fired, while component is active.")
    @DesignerXMLProperty(functionalArea = BEHAVIOR, priority = 110)
    private ActionBinding onInput;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Id of formatter which will format object to String. It must be consistent with value of pl.fhframework.formatter.FhFormatter annotation.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 93)
    private String formatter;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FORMATTER_BINDING)
    @DocumentedComponentAttribute(boundable = true, value = "Id of formatter which will format object to String. It must be consistent with value of pl.fhframework.formatter.FhFormatter annotation.")
    @DesignerXMLProperty(functionalArea = SPECIFIC)
    private ModelBinding formatterBinding;

    @Getter
    private String language;

    public InputText(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        updateRegexPattern();
    }

    private void updateRegexPattern() {
        if (requiredRegex != null) {
            try {
                requiredRegexPattern = Pattern.compile(requiredRegex);
            } catch (PatternSyntaxException e) {
                FhLogger.error("Invalid pattern " + requiredRegex);
            }
        }
        else {
            requiredRegexPattern = null;
        }
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ON_INPUT_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onInput);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @JsonIgnore
    @Override
    public Optional<String> getOptionalFormatter() {
        return Optional.ofNullable(this.formatter);
    }

    @Override
    public InputText createNewSameComponent() {
        return new InputText(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, BaseInputField baseClone) {
        super.doCopy(table, iteratorReplacements, baseClone);
        InputText clone = (InputText) baseClone;
        clone.setPlaceholder(getPlaceholder());
        clone.setRowsCount(getRowsCount());
        clone.setMaxLength(getMaxLength());
        clone.setMask(getMask());
        clone.setMaskDefinition(getMaskDefinition());
        clone.setMaskDynamic(getMaskDynamic());
        clone.setMaskBinding(table.getRowBinding(getMaskBinding(), clone, iteratorReplacements));
        clone.setInputType(getInputType());
        clone.setOnInput(table.getRowBinding(getOnInput(), clone, iteratorReplacements));
        clone.setFormatter(getFormatter());
        clone.setMaxLengthBinding(table.getRowBinding(getMaxLengthBinding(), clone, iteratorReplacements));
        clone.setFormatterBinding(table.getRowBinding(getFormatterBinding(), clone, iteratorReplacements));
        clone.setRequiredRegexBinding(table.getRowBinding(getRequiredRegexBinding(), clone, iteratorReplacements));
    }

    @Override
    public void setBody(String body) {
        setModelBindingAdHoc(body);
    }

    @Override
    public String getBodyAttributeName() {
        return VALUE_ATTR;
    }

    private void processMask(ElementChanges elementChanges) {
        if (maskBinding == null) {
            return;
        }

        BindingResult maskBindingResult = maskBinding.getBindingResult();
        if (maskBindingResult != null) {
            String mask = this.convertValueToString(maskBindingResult.getValue());

            if (!areValuesTheSame(mask, this.mask)) {
                this.refreshView();
                this.mask = mask;
                elementChanges.addChange(MASK, this.mask);
            }
        }
    }

    private void processMaxLength(ElementChanges elementChanges) {
        if (maxLengthBinding == null) {
            if (maxLength == null) {
                maxLength = BasicControlsConfiguration.getInstance().getInputTextMaxLength();
            }
            return;
        }

        BindingResult maxLengthBindingResult = maxLengthBinding.getBindingResult();
        if (maxLengthBindingResult != null) {
            Integer maxLength = this.convertValue(maxLengthBindingResult.getValue(), Integer.class);

            if (!areValuesTheSame(maxLength, this.maxLength)) {
                this.refreshView();
                this.maxLength = maxLength;
                elementChanges.addChange(MAX_LENGTH_BINDING, this.maxLength);
            }
        }
    }

    private void processFormatter() {
        if (formatterBinding == null) {
            return;
        }

        BindingResult formatterBindingResult = formatterBinding.getBindingResult();
        if (formatterBindingResult != null) {
            Object value = formatterBindingResult.getValue();
            String formatter;
            if (value == null) {
                formatter = null;
            } else {
                formatter = this.convertValueToString(value);
            }

            if (!areValuesTheSame(formatter, this.formatter)) {
                this.refreshView();
                this.formatter = formatter;
            }
        }
    }

    private void processRequiredRegex() {
        if (requiredRegexBinding == null) {
            return;
        }

        BindingResult requiredRegexBindingResult = requiredRegexBinding.getBindingResult();
        if (requiredRegexBindingResult != null) {
            Object value = requiredRegexBindingResult.getValue();
            String requiredRegex;
            if (value == null) {
                requiredRegex = null;
            } else {
                requiredRegex = this.convertValueToString(value);
            }

            if (!areValuesTheSame(requiredRegex, this.requiredRegex)) {
                this.refreshView();
                this.requiredRegex = requiredRegex;
                updateRegexPattern();
            }
        }
    }

    @Override
    public ElementChanges updateView() {
        processFormatter();
        final ElementChanges elementChanges = super.updateView();
        processMask(elementChanges);
        processMaxLength(elementChanges);
        processRequiredRegex();
        this.language = LanguageResolver.languageChanges(getForm().getAbstractUseCase().getUserSession(), this.language, elementChanges);
        return elementChanges;
    }

    @Override
    protected ValidationManager<BaseInputField> createValidationManager() {
        return ValidationFactory.getInstance().getInputTextValidationProcess();
    }

    public void setOnInput(ActionBinding onInput) {
        this.onInput = onInput;
    }

    public IActionCallbackContext setOnInput(IActionCallback onInput) {
        return CallbackActionBinding.createAndSet(onInput, this::setOnInput);
    }
}
