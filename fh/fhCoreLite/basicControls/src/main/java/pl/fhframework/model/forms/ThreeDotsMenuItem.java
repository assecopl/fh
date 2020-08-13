package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;

@Control(parents = {Dropdown.class, Repeater.class}, canBeDesigned = true)
public class ThreeDotsMenuItem extends DropdownItem implements Iconable {
    public ThreeDotsMenuItem(Form form) {
        super(form);
    }
}
