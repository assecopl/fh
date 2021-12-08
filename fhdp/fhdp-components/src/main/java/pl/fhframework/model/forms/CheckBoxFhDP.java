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

@TemplateControl(tagName = "input-checkbox-fhdp")
@DesignerControl(defaultWidth = 2)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, documentationExample = true, value = "CheckBox is component which let a user select MANY of a limited number of choices. It is" +
        " displayed as HTML input type = checkbox element.", icon = "fa fa-check-square")
@Control(parents = {PanelGroup.class, Group.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = Boolean.class, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))
public class CheckBoxFhDP extends CheckBox {
    public static final String ATTR_IS_DEFAULT_STYLE = "isDefaultStyle";
    public static final String ATTR_IS_TRI_STATE = "isTriState";

    @Getter
    private Boolean isDefaultStyle;

    @Getter
    private Boolean isTriState;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_IS_DEFAULT_STYLE) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Enable default style.")
    private ModelBinding<Boolean> isDefaultStyleModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_IS_TRI_STATE) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Enable tri state.")
    private ModelBinding<Boolean> isTriStateModelBinding;

    public CheckBoxFhDP(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();

        if (isDefaultStyle != null) {
            BindingResult bidingResult = isDefaultStyleModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.isDefaultStyle = convertValue(bidingResult.getValue(), Boolean.class);
                }
            }
        }
        if (isTriState != null) {
            BindingResult bidingResult = isTriStateModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.isTriState = convertValue(bidingResult.getValue(), Boolean.class);
                }
            }
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if(isDefaultStyleModelBinding != null) {
            BindingResult bindingResult = isDefaultStyleModelBinding.getBindingResult();
            this.isDefaultStyle = convertValue(bindingResult.getValue(), Boolean.class);
        }
        if(isTriStateModelBinding != null) {
            BindingResult bindingResult = isTriStateModelBinding.getBindingResult();
            this.isTriState = convertValue(bindingResult.getValue(), Boolean.class);
        }

        return  elementChange;
    }
}
