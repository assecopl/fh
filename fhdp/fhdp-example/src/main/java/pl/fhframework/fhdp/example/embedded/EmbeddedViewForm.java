package pl.fhframework.fhdp.example.embedded;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Form;

public class EmbeddedViewForm extends Form<EmbeddedViewForm.Model> {
    @Getter
    @Setter
    public static class Model {
        private String sampleField;
        private String sampleComboField;
        private String content = "<p><b style=\"background-color: rgb(255, 0, 0);\">Some text</b></p><p>There are <span style=\"color: rgb(0, 0, 255);\">implemented</span> new <span style=\"background-color: rgb(231, 148, 57);\">system</span>...&nbsp;</p>";
        private String exampleXML;
        private String pickedCountryCode;
        private String reportData;
        private String currentLink;
    }

    public EmbeddedViewForm() {
        setStyleClasses("embeddedViewForm");
        setWrapperStyle("embeddedViewFormWrapper");
    }
}
