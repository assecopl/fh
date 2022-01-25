
package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Form;

public class AppTitleForm extends Form<AppTitleForm.Model> {

    @Getter
    @Setter
    public static class Model {
        private String buildBranch;
        private String buildNumber;
        private String environment;
        private String subVersion;
        private String appName;
        private boolean versionVisibility;
        private String version;
    }

    public AppTitleForm() {
        setStyleClasses("form-transparent");
    }
}