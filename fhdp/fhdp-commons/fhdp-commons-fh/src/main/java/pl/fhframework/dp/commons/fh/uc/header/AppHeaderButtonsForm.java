package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.Form;

public class AppHeaderButtonsForm extends Form<AppHeaderButtonsForm.Model> {
    @Getter
    @Setter
    public static class Model {
        AccessibilityEnum sideBarButtonAvailability = AccessibilityEnum.EDIT;
    }

    public AppHeaderButtonsForm() {
        setStyleClasses("form-transparent");
    }
}
