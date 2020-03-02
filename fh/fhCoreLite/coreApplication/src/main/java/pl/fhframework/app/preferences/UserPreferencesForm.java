package pl.fhframework.app.preferences;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Form;

public class UserPreferencesForm extends Form<UserPreferencesForm.Model> {
    @Getter
    @Setter
    public static class Model {
        private final boolean falseValue = false;
        private final boolean trueValue = true;

        private boolean useDefaultPrefs = true;
        private boolean hideInnerPaddings;
        private boolean editActiveElement;
        private boolean filterOutRowFromToolbox;

        private int designerWidth = 1;
    }
}
