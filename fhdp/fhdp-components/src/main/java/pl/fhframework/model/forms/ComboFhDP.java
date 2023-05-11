package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

@TemplateControl(tagName = "fh-combo-fhdp")
@DesignerControl(defaultWidth = 3)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
        icon = "fa fa-outdent")
@Control(parents = {PanelGroup.class, Group.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class},
        invalidParents = {Table.class},
        canBeDesigned = true)
public class ComboFhDP extends Combo {
    public static final String ATTR_LAST_VALUE = "lastValue";
    public static final String ATTR_HIDE_CROSSED = "hideCrossed";
    public static final String ATTR_NEW_VALUE_TEXT = "newValueText";
    public static final String ATTR_IS_LAST_VALUE_ENABLED = "isLastValueEnabled";
    public static final String ATTR_IS_TABLE_MODE = "isTableMode";

    @Getter
    private String lastValue;
    @Getter String hideCrossed;
    @Getter
    private String newValueText;
    @Getter
    private Boolean isLastValueEnabled;
    @Getter
    private Boolean isTableMode;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LAST_VALUE) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Last value")
    private ModelBinding lastValueModelBinding;

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
    @XMLProperty(required = true, value = ATTR_IS_TABLE_MODE)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Use textarea instead of input. Text wrapping for table.")
    private ModelBinding<Boolean> isTableModeModelBinding;

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
    @DocumentedComponentAttribute(boundable = true, value = "For hide crossed out")
    private ModelBinding hideCrossedModelBinding;

    public ComboFhDP(Form form) {
        super(form);
    }

    @Override
    public void init(){
        super.init();
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
        if (isTableMode != null) {
            BindingResult bidingResult = isTableModeModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.isTableMode = convertValue(bidingResult.getValue(), Boolean.class);
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
            elementChange.addChange(ATTR_NEW_VALUE_TEXT, this.newValueText);
        }
        if(isLastValueEnabledModelBinding != null) {
            BindingResult bindingResult = isLastValueEnabledModelBinding.getBindingResult();
            this.isLastValueEnabled = convertValue(bindingResult.getValue(), Boolean.class);
        }
        if(isTableModeModelBinding != null) {
            BindingResult bindingResult = isTableModeModelBinding.getBindingResult();
            this.isTableMode = convertValue(bindingResult.getValue(), Boolean.class);
        }
        if (lastValueModelBinding != null) {
            BindingResult bindingResult = lastValueModelBinding.getBindingResult();

            String newRaw = convertToRaw(bindingResult);
            this.lastValue = newRaw;
            elementChange.addChange(ATTR_LAST_VALUE, this.lastValue);
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
