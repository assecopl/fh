package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.IDesignerEventListener;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.util.Objects;
import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Group.class}, canBeDesigned = true)
@DocumentedComponent(value = "PanelGroup component responsible for the grouping of sub-elements, only one uncollapsed group will be allowed.", icon = "fa fa-caret-down")
public class Accordion extends GroupingComponent<FormElement> implements Boundable, IChangeableByClient, CompactLayout, IDesignerEventListener {

    private static final String ATTR_ACTIVE_GROUP = "activeGroup";
    public static final String ATTR_ON_GROUP_CHANGE = "onGroupChange";
    public static final Integer DEFAULT_ACTIVE_GROUP = null;

    @Getter
    private Integer activeGroup = DEFAULT_ACTIVE_GROUP;

    @Getter
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @XMLProperty
    @DocumentedComponentAttribute(value = "If there is some value, representing method in use case, then on clicking on group, " +
            "that method will be executed. This method fires, when group will be opened.")
    private ActionBinding onGroupChange;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_ACTIVE_GROUP)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    @DocumentedComponentAttribute(defaultValue = "0", boundable = true, value = "Index of uncollapsed tab.")
    private ModelBinding<Integer> activeGroupBinding;

    public Accordion(Form form) {
        super(form);
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        String newValue = valueChange.getMainValue();
        Integer newActiveGroup = Integer.parseInt(newValue);
        if (!Objects.equals(newActiveGroup, activeGroup)) {
            this.activeGroup = newActiveGroup;
        }
        if (activeGroupBinding != null) {
            this.updateBindingForValue(newActiveGroup, activeGroupBinding, activeGroup);
        }
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        if (activeGroupBinding != null) {
            BindingResult bindingResult = activeGroupBinding.getBindingResult();
            if (bindingResult != null) {
                Object value = bindingResult.getValue();
                if (value != null) {
                    Integer newActiveGroup = this.convertValue(value, Integer.class);
                    if (!Objects.equals(newActiveGroup, activeGroup)) {
                        refreshView();
                        this.activeGroup = newActiveGroup;
                        elementChanges.addChange(ATTR_ACTIVE_GROUP, this.activeGroup);
                    }
                }
            }
        }
        return elementChanges;
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ATTR_ON_GROUP_CHANGE.equals(eventData.getEventType())) {
            return Optional.ofNullable(onGroupChange);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public void onDesignerAddDefaultSubcomponent(SpacerService spacerService) {
        addSubcomponent(createExamplePanel(getSubcomponents().size() + 1, spacerService));
    }

    @Override
    public void onDesignerBeforeAdding(IGroupingComponent<?> parent, SpacerService spacerService) {
        addSubcomponent(createExamplePanel(1, spacerService));
        addSubcomponent(createExamplePanel(2, spacerService));
    }

    private PanelGroup createExamplePanel(int nameSuffix, SpacerService spacerService) {
        PanelGroup panel = new PanelGroup(getForm());
        panel.setLabelModelBinding(new StaticBinding<>("Accordion Panel " + nameSuffix));
        panel.setGroupingParentComponent(this);
        panel.init();
        return panel;
    }

    public void setOnGroupChange(ActionBinding onGroupChange) {
        this.onGroupChange = onGroupChange;
    }

    public IActionCallbackContext setOnGroupChange(IActionCallback onGroupChange) {
        return CallbackActionBinding.createAndSet(onGroupChange, this::setOnGroupChange);
    }
}
