package pl.fhframework.app.menu;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Form;

import java.util.List;

public class NavbarForm extends Form<NavbarForm.Model> {
    @Getter
    @Setter
    public static class Model {
        private String login;
        private boolean guest;
        private String alternativeStylesheet;
        private String language = Language.ENGLISH.getValue();
        private String logoutURL;
        private String loginURL;
        private boolean fhCss;
        private boolean defaultCss;
        private List<String> cssIds;

        public String getId(int idx) {
            if (cssIds.size() > idx) {
                return cssIds.get(idx);
            }

            return "";
        }
    }

    public enum Language {
        POLISH("pl"), ENGLISH("en");

        private String value;

        Language(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
