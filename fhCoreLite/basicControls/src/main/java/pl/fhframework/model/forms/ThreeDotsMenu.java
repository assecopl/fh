package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.model.forms.designer.IDesignerEventListener;

@Control(parents = {ButtonGroup.class, PanelGroup.class, Group.class, Tab.class, Row.class, Form.class, Repeater.class, Column.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DesignerControl(defaultWidth = 2)
@DocumentedComponent(value = "Button component responsible for the grouping of ThreeDotsMenuItems in a table row.", icon = "fas fa-ellipsis-v")
public class ThreeDotsMenu extends Dropdown implements Boundable, Styleable, IHasBoundableLabel, CompactLayout, IDesignerEventListener {

    @Getter
    private String label = "[icon='fas fa-ellipsis-v']";

    @Getter
    @Setter
    private Style style = Style.LIGHT;

    public ThreeDotsMenu(Form form) {
        super(form);
    }


    @Override
    public void onDesignerAddDefaultSubcomponent(SpacerService spacerService) {
        addSubcomponent(createExampleDropdownItem(getSubcomponents().size() + 1));
    }

    @Override
    public void onDesignerBeforeAdding(IGroupingComponent<?> parent, SpacerService spacerService) {
        addSubcomponent(createExampleDropdownItem(1));
        addSubcomponent(createExampleDropdownItem(2));
        addSubcomponent(createExampleDropdownItem(3));
    }

    private ThreeDotsMenuItem createExampleDropdownItem(int nameSuffix) {
        ThreeDotsMenuItem tab = new ThreeDotsMenuItem(getForm());
        tab.setModelBindingForValue(new StaticBinding<>("Dropdown item " + nameSuffix));
        tab.setGroupingParentComponent(this);
        tab.init();
        return tab;
    }
}
