package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02/06/2020
 */
@DesignerControl(defaultWidth = 3)
@TemplateControl(tagName = "input-text-fhdp")
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, value = "InputText component is responsible for displaying simple field, where user can write some data" +
        " plus label representing this field.", icon = "fa fa-edit")
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = String.class, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))

public class InputTextFhDP extends InputText{
    public static final String ATTR_LAST_VALUE = "lastValue";
    public static final String ATTR_HIDE_CROSSED = "hideCrossed";
    public static final String ATTR_NEW_VALUE_TEXT = "newValueText";
    public static final String ATTR_IS_LAST_VALUE_ENABLED = "isLastValueEnabled";
    public static final String ATTR_IS_CHARACTER_COUNTER = "isCharacterCounter";
    public static final String ATTR_CHARACTER_COUNTER_STYLE_CLASSES = "characterCounterStyleClasses";
    public static final String ATTR_TEXT_BEFORE_CHARACTER_COUNTER = "textBeforeCharacterCounter";

    @Getter
    private String lastValue;

    @Getter String hideCrossed;

    @Getter
    private String newValueText;

    @Getter
    private Boolean isLastValueEnabled;

    @Getter
    private Boolean isCharacterCounter;

    @Getter
    private String characterCounterStyleClasses;

    @Getter
    private String textBeforeCharacterCounter;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LAST_VALUE) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding<String> lastValueModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_IS_LAST_VALUE_ENABLED) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Display only code, there will be no method invoke from provider.")
    private ModelBinding<Boolean> isLastValueEnabledModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_IS_CHARACTER_COUNTER) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Display character counter, there will be no method invoke from provider.")
    private ModelBinding<Boolean> isCharacterCounterModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_CHARACTER_COUNTER_STYLE_CLASSES) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding<String> characterCounterStyleClassesTextModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TEXT_BEFORE_CHARACTER_COUNTER) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding<String> textBeforeCharacterCounterModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_NEW_VALUE_TEXT) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding<String> newValueTextModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_HIDE_CROSSED) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding hideCrossedModelBinding;

    public InputTextFhDP(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        /**
         * Podczas inicjalizacji komponentu realizujemy binding wartości z modelu danych.
         */

        if (newValueText != null) {
            BindingResult bidingResult = newValueTextModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.newValueText = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }

        if (lastValue != null) {
            BindingResult bidingResult = lastValueModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.lastValue = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }

        if (isLastValueEnabled != null) {
            BindingResult bidingResult = isLastValueEnabledModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.isLastValueEnabled = convertValue(bidingResult.getValue(), Boolean.class);
                }
            }
        }

        if (isCharacterCounter != null) {
            BindingResult bidingResult = isCharacterCounterModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.isCharacterCounter = convertValue(bidingResult.getValue(), Boolean.class);
                }
            }
        }

        if (characterCounterStyleClasses != null) {
            BindingResult bidingResult = characterCounterStyleClassesTextModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.characterCounterStyleClasses = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }

        if (textBeforeCharacterCounter != null) {
            BindingResult bidingResult = textBeforeCharacterCounterModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.textBeforeCharacterCounter = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }

        if(hideCrossed != null){
            BindingResult bindingResult = hideCrossedModelBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.hideCrossed = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
    }
    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if(newValueTextModelBinding != null) {
            BindingResult bindingResult = newValueTextModelBinding.getBindingResult();
            this.newValueText = convertValue(bindingResult.getValue(), String.class);
        }
        if(isLastValueEnabledModelBinding != null) {
            BindingResult bindingResult = isLastValueEnabledModelBinding.getBindingResult();
            this.isLastValueEnabled = convertValue(bindingResult.getValue(), Boolean.class);
        }
        if(isCharacterCounterModelBinding != null) {
            BindingResult bindingResult = isCharacterCounterModelBinding.getBindingResult();
            this.isCharacterCounter = convertValue(bindingResult.getValue(), Boolean.class);
        }
        if(characterCounterStyleClassesTextModelBinding != null) {
            BindingResult bindingResult = characterCounterStyleClassesTextModelBinding.getBindingResult();
            this.characterCounterStyleClasses = convertValue(bindingResult.getValue(), String.class);
        }
        if(textBeforeCharacterCounterModelBinding != null) {
            BindingResult bindingResult = textBeforeCharacterCounterModelBinding.getBindingResult();
            this.textBeforeCharacterCounter = convertValue(bindingResult.getValue(), String.class);
        }
        if (lastValueModelBinding != null) {
            lastValue = lastValueModelBinding.resolveValueAndAddChanges(this, elementChange, lastValue, ATTR_LAST_VALUE);
        }
        if (hideCrossedModelBinding != null) {
            BindingResult bindingResult = hideCrossedModelBinding.getBindingResult();

            String newRaw = convertToRaw(bindingResult);
            this.hideCrossed = newRaw;
            elementChange.addChange(ATTR_HIDE_CROSSED, this.hideCrossed);

        }

        return elementChange;
    }
}
