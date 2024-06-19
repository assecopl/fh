package pl.fhframework.docs.availability.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.AccessibilityEnum;

/**
 * Created by k.czajkowski on 15.03.2017.
 */
@Getter
@Setter
public class PropertyElement {
    private boolean editable;
    private AccessibilityEnum availability = AccessibilityEnum.EDIT;

    public PropertyElement(boolean editable) {
        this.editable = editable;
    }
}
