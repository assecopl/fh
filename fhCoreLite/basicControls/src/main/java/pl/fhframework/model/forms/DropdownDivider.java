package pl.fhframework.model.forms;

import pl.fhframework.annotations.Control;

@Control(parents = {Dropdown.class, Repeater.class}, canBeDesigned = false)
public class DropdownDivider extends FormElement {
    public DropdownDivider(Form form) {
        super(form);
    }
}
