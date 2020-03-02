package pl.fhframework.model.forms;

import lombok.Getter;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.CallbackActionBinding;
import pl.fhframework.binding.IActionCallback;
import pl.fhframework.binding.IActionCallbackContext;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.forms.designer.IDesignerEventListener;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

/**
 * Group component is responsible for placing components in one group, that does not intersects with
 * other form components.
 *
 * Example: <Group></Group>
 */
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class, SplitContainer.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(value = "Group component is responsible for placing components in one group, that does not intersects with other form components", icon = "fa fa-columns")
public class Group extends GroupingComponent<Component> implements IDesignerEventListener {

    private static final String ATTR_ON_CLICK = "onClick";

    @Getter
    @XMLProperty
    @DesignerXMLProperty(commonUse = false, functionalArea = BEHAVIOR, priority = 100)
    @DocumentedComponentAttribute(value = "If the group is clicked that method will be executed. All components inside that group with actions will be invoked first." +
            " The action may or may not be propagated to Group. It depends on used component, so use it with caution.")
    private ActionBinding onClick;

    public Group(Form form) {
        super(form);
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ATTR_ON_CLICK.equals(eventData.getEventType())) {
            return Optional.ofNullable(onClick);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    public void setOnClick(ActionBinding onClick) {
        this.onClick = onClick;
    }

    public IActionCallbackContext setOnClick(IActionCallback onClick) {
        return CallbackActionBinding.createAndSet(onClick, this::setOnClick);
    }


    @Override
    public Boolean isDesignDeletable(){
        if(this.getGroupingParentComponent() instanceof SplitContainer) {
            return false;
        }
        return true;
    }

    @Override
    public void onDesignerAddDefaultSubcomponent(SpacerService spacerService) {
        addSubcomponent(createNewRow());
    }

    @Override
    public void onDesignerBeforeAdding(IGroupingComponent<?> parent, SpacerService spacerService) {
        addSubcomponent(createNewRow());
    }

    private Row createNewRow() {
        Row row = new Row(getForm());
        row.setGroupingParentComponent(this);
        row.init();
        return row;
    }

}
