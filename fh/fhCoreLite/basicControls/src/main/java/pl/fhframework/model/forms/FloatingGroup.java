package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.attributes.AttributeHolder;
import pl.fhframework.model.forms.attributes.AttributeHolderBuilder;
import pl.fhframework.model.forms.attributes.FloatingGroupStateAttribute;
import pl.fhframework.model.forms.attributes.FloatingOnlyAttribute;
import pl.fhframework.model.forms.model.FloatingPinMode;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.ARRANGEMENT, value = "PanelGroup component responsible for the grouping of sub-elements and can be pinned out from document.", icon = "fa fa-object-group")
public class FloatingGroup extends PanelGroup {

    public static final String ON_PIN_ATTR = "onTogglePin";
    public static final String ON_TOGGLE_FULL_SCREEN_ATTR = "onToggleFullScreen";

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute("Floating group offset from top of the screen (floating state only). Negative value would be substracted from the height of the screen.")
    private String top;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute("Floating group offset from left side of the screen (floating state only). Negative value would be substracted from the width of the screen.")
    private String left;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute("Floating group height (floating state only). Negative value would be substracted from the height of the screen. Defaults to height attribute value.")
    private String floatingHeight;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute("Floating group width (floating state only). Negative value would be substracted from the width of the screen. Defaults to width attribute value.")
    private String floatingWidth;

    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty
    @DocumentedComponentAttribute("Name of action which will be invoked after pinning/unpinning")
    private ActionBinding onTogglePin;

    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty
    @DocumentedComponentAttribute("Name of action which will be invoked after toggling full screen")
    private ActionBinding onToggleFullScreen;

    @JsonUnwrapped
    @Getter
    @DocumentedAttributesHolder(attributeClasses = {FloatingOnlyAttribute.class, FloatingGroupStateAttribute.class})
    private AttributeHolder attributeHolder;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(FloatingOnlyAttribute.FLOATING_ONLY_ATTR)
    private ModelBinding<Boolean> floatingOnlyBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FloatingGroupStateAttribute.STATE_ATTR, defaultValue = "PINNED_MINIMIZED")
    @DesignerXMLProperty(allowedTypes = FloatingGroupStateAttribute.FloatingState.class)
    private ModelBinding<FloatingGroupStateAttribute.FloatingState> floatingGroupStateBinding;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute("Determines if pinning/fullscreen buttons are hidden.")
    private boolean hideButtons;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute("Determines if header with label and buttons is hidden.")
    private boolean hideHeader;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "normal")
    @DocumentedComponentAttribute(defaultValue = "normal", value = "Pinning mode of the floating group. Valid values are: normal, button, invisible")
    private FloatingPinMode pinningMode = FloatingPinMode.normal;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "true")
    @DocumentedComponentAttribute(defaultValue = "true", value = "Determine if user can change position of an element by dragging it.")
    private Boolean isDraggable = true;

    public FloatingGroup(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        FloatingOnlyAttribute floatingOnlyAttribute = new FloatingOnlyAttribute(getForm(), this, floatingOnlyBinding);
        attributeHolder = new AttributeHolderBuilder()
                .attribute(floatingOnlyAttribute)
                .attribute(new FloatingGroupStateAttribute(getForm(), this, floatingGroupStateBinding, floatingOnlyAttribute))
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
        super.updateModel(valueChange);
        attributeHolder.updateModel(getForm(), valueChange);
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ON_PIN_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onTogglePin);
        } else if (ON_TOGGLE_FULL_SCREEN_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onToggleFullScreen);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    public void setOnTogglePin(ActionBinding onTogglePin) {
        this.onTogglePin = onTogglePin;
    }

    public IActionCallbackContext setOnTogglePin(IActionCallback onTogglePin) {
        return CallbackActionBinding.createAndSet(onTogglePin, this::setOnTogglePin);
    }

    public void setOnToggleFullScreen(ActionBinding onToggleFullScreen) {
        this.onToggleFullScreen = onToggleFullScreen;
    }

    public IActionCallbackContext setOnToggleFullScreen(IActionCallback onToggleFullScreen) {
        return CallbackActionBinding.createAndSet(onToggleFullScreen, this::setOnToggleFullScreen);
    }

}
