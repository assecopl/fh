package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Setter;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedAttributesHolder;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.attributes.AttributeHolder;
import pl.fhframework.model.forms.attributes.AttributeHolderBuilder;
import pl.fhframework.model.forms.attributes.grid.EditModeAttribute;
import pl.fhframework.model.forms.attributes.grid.OnToggleAddAttribute;
import pl.fhframework.model.forms.attributes.grid.OnToggleEditAttribute;
import pl.fhframework.model.dto.InMessageEventData;

import lombok.Getter;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

@Control(parents = {PanelGroup.class, Row.class, Form.class, Group.class}, canBeDesigned = true)
public class Dashboard extends GroupingComponent<Component> implements IChangeableByClient {

    @JsonUnwrapped
    @Getter
    @DocumentedAttributesHolder(attributeClasses = {EditModeAttribute.class})
    private AttributeHolder attributeHolder;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(EditModeAttribute.EDIT_MODE_ATTR)
    private ModelBinding editModeBinding;

    @JsonIgnore
    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty(value = OnToggleEditAttribute.ON_TOGGLE_EDIT_ATTR)
    private ActionBinding onToggleEdit;

    @JsonIgnore
    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty(value = OnToggleAddAttribute.ON_TOGGLE_ADD_ATTR)
    private ActionBinding onToggleAdd;

    public Dashboard(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();

        attributeHolder = new AttributeHolderBuilder()
                .attribute(new EditModeAttribute(getForm(), this, editModeBinding))
                .build();
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        attributeHolder.updateView(this, elementChanges);
        return elementChanges;
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        attributeHolder.updateModel(getForm(), valueChange);
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (OnToggleEditAttribute.ON_TOGGLE_EDIT_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onToggleEdit);
        } else if (OnToggleAddAttribute.ON_TOGGLE_ADD_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onToggleAdd);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    public void setOnToggleEdit(ActionBinding onToggleEdit) {
        this.onToggleEdit = onToggleEdit;
    }

    public IActionCallbackContext setOnToggleEdit(IActionCallback onToggleEdit) {
        return CallbackActionBinding.createAndSet(onToggleEdit, this::setOnToggleEdit);
    }

    public void setOnToggleAdd(ActionBinding onToggleAdd) {
        this.onToggleAdd = onToggleAdd;
    }

    public IActionCallbackContext setOnToggleAdd(IActionCallback onToggleAdd) {
        return CallbackActionBinding.createAndSet(onToggleAdd, this::setOnToggleAdd);
    }
}
