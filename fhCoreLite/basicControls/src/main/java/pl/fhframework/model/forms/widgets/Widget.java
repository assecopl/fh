package pl.fhframework.model.forms.widgets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.*;
import pl.fhframework.model.forms.attributes.AttributeHolder;
import pl.fhframework.model.forms.attributes.AttributeHolderBuilder;
import pl.fhframework.model.forms.attributes.widget.SizeXAttribute;
import pl.fhframework.model.forms.attributes.widget.SizeYAttribute;
import pl.fhframework.model.forms.attributes.widget.XPosAttribute;
import pl.fhframework.model.forms.attributes.widget.YPosAttribute;

import lombok.Getter;

@Control(parents = {Accordion.class, PanelGroup.class, Group.class, SplitContainer.class, Row.class, Form.class, Tab.class, Repeater.class}, invalidParents = {Table.class})
@DocumentedComponent(value = "Widget component responsible for the grouping of sub-elements", icon = "fa fa-qrcode")
public class Widget extends PanelGroup {

    private static final String DESCRIPTION_ATTR = "description";

    @JsonUnwrapped
    @Getter
    @DocumentedAttributesHolder(attributeClasses = {SizeXAttribute.class, SizeYAttribute.class, XPosAttribute.class, YPosAttribute.class})
    private AttributeHolder attributeHolder;

    @XMLProperty
    @Getter
    @Setter
    @JsonIgnore
    private String description = "";

    @Getter
    @Setter
    private boolean markAsDeleted;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = SizeXAttribute.SIZE_X_ATTR)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    private ModelBinding sizeXBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = SizeYAttribute.SIZE_Y_ATTR)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    private ModelBinding sizeYBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = XPosAttribute.POSITION_X_ATTR)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    private ModelBinding positionXBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = YPosAttribute.POSITION_Y_ATTR)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    private ModelBinding positionYBinding;


    public Widget(Form form) {
        super(form);
    }

    public void init() {
        super.init();
        attributeHolder = new AttributeHolderBuilder()
                .attribute(new SizeXAttribute(getForm(), this, sizeXBinding))
                .attribute(new SizeYAttribute(getForm(), this, sizeYBinding))
                .attribute(new XPosAttribute(getForm(), this, positionXBinding))
                .attribute(new YPosAttribute(getForm(), this, positionYBinding))
                .build();
    }

    @Override
    public ElementChanges updateView() {
        final ElementChanges elementChanges = super.updateView();
        if (attributeHolder != null) {
            attributeHolder.updateView(this, elementChanges);
        }
        return elementChanges;
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        super.updateModel(valueChange);
        if (attributeHolder != null) {
            attributeHolder.updateModel(getForm(), valueChange);
        }
        final Boolean markAsDeleted = valueChange.getBooleanAttribute("markAsDeleted");
        if (Boolean.TRUE.equals(markAsDeleted)) {
            this.markAsDeleted = markAsDeleted.booleanValue();
        }
    }
}
