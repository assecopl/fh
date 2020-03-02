package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.IDesignerEventListener;
import pl.fhframework.model.dto.InMessageEventData;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

/**
 * Created by k.czajkowski on 03.01.2017.
 */
@TemplateControl(tagName = "fh-button-group")
@Control(parents = {PanelGroup.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.BUTTONS_AND_OTHER, value = "PanelGroup component responsible for the grouping of buttons.", icon = "fa fa-square")
public class ButtonGroup extends GroupingComponent<FormElement> implements Boundable, IChangeableByClient, CompactLayout, IDesignerEventListener {

    private static final String ATTR_ACTIVE_BUTTON = "activeButton";
    public static final String ATTR_ON_BUTTON_CHANGE = "onButtonChange";
    private static final String DEFAULT_ACTION_NAME = "-";

    @Getter
    private int activeButton = -1;

    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty(defaultValue = DEFAULT_ACTION_NAME)
    @DocumentedComponentAttribute(value = "If there is some value, representing method in use case, then on clicking on button, that method will be executed. This method fires, when button is clicked.")
    private ActionBinding onButtonChange;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_ACTIVE_BUTTON)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    @DocumentedComponentAttribute(boundable = true, value = "Index of active Button. Default value of active button is -1, that means ButtonGroup does not have active button by default.")
    private ModelBinding activeButtonBinding;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Flag decides if margin should be displayed or not.", defaultValue = "false")
    private boolean margin;

    public ButtonGroup(Form form) {
        super(form);
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        String newValue = valueChange.getMainValue();
        int newActiveButton = Integer.parseInt(newValue);
        if (newActiveButton != this.activeButton) {
            this.activeButton = newActiveButton;
            this.updateBindingForValue(newActiveButton, activeButtonBinding, activeButton);
        }
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        if (activeButtonBinding != null) {
            BindingResult bindingResult = this.activeButtonBinding.getBindingResult();
            int numberOfButtons = this.getSubcomponents().size();
            if (bindingResult != null) {
                Object value = bindingResult.getValue();
                if (value != null) {
                    int newActiveButton = this.convertValue(value, Integer.class);
                    this.activeButton = (newActiveButton > numberOfButtons - 1) ? new Integer(-1) : newActiveButton;
                    refreshView();
                    elementChanges.addChange(ATTR_ACTIVE_BUTTON, this.activeButton);
                }
            }
        }
        return elementChanges;
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ATTR_ON_BUTTON_CHANGE.equals(eventData.getEventType())) {
            return Optional.ofNullable(onButtonChange);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public void onDesignerAddDefaultSubcomponent(SpacerService spacerService) {
        addSubcomponent(createExampleButton(getSubcomponents().size() + 1));
    }

    @Override
    public void onDesignerBeforeAdding(IGroupingComponent<?> parent, SpacerService spacerService) {
        addSubcomponent(createExampleButton(1));
        addSubcomponent(createExampleButton(2));
    }

    public void setOnButtonChange(ActionBinding onButtonChange) {
        this.onButtonChange = onButtonChange;
    }

    public IActionCallbackContext setOnButtonChange(IActionCallback onButtonChange) {
        return CallbackActionBinding.createAndSet(onButtonChange, this::setOnButtonChange);
    }

    private Button createExampleButton(int nameSuffix) {
        Button button = new Button(getForm());
        button.setLabelModelBinding(new StaticBinding<>("Button " + nameSuffix));
        button.setGroupingParentComponent(this);
        button.init();
        return button;
    }
}
