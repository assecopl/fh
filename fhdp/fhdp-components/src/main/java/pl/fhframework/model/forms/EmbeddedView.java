package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.model.LabelPosition;
import pl.fhframework.model.forms.widgets.Widget;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.LOOK_AND_STYLE;

@Getter
@Setter
@DesignerControl(defaultWidth = 12)
@Control(parents = {PanelGroup.class, Tab.class, Row.class, Form.class, Group.class}, invalidParents = {Table.class, Widget.class, Repeater.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.IMAGE_HTML_MD, value = "Embedded iframe view", icon = "fa fa-eye")
public class EmbeddedView extends FormElement {

    public static final String ATTR_SRC = "src";

    @Getter
    private String src;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_SRC) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding<String> srcModelBinding;


    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 95)
    @DocumentedComponentAttribute(value = "Defines position of a label. Position is one of: up, down, left, right.")
    private LabelPosition labelPosition;


    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(priority = 100, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Represents label for created component. Supports FHML - FH Markup Language.")
    private String label;

    public EmbeddedView(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        /**
         * Podczas inicjalizacji komponentu realizujemy binding wartości z modelu danych.
         */
        if (srcModelBinding != null) {
            BindingResult bidingResult = srcModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.src = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }

    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (srcModelBinding != null) {
            src = srcModelBinding.resolveValueAndAddChanges(this, elementChange, src, ATTR_SRC);
        }
        return elementChange;
    }

}
