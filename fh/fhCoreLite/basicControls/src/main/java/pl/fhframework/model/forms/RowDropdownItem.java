package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;

@Control(parents = {Dropdown.class, Repeater.class}, canBeDesigned = true)
public class RowDropdownItem extends DropdownItem implements Iconable {
    public RowDropdownItem(Form form) {
        super(form);
    }
}
