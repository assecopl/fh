package pl.fhframework.fhdp.example.fhml;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Form;

import java.util.ArrayList;


public class ExtendedFHMLViewForm extends Form<ExtendedFHMLViewForm.Model> {
    @Getter
    @Setter
    public static class Model {
        private String sampleField;
        private String sampleComboField;
        private int sampleNumber = 5;
    }

    public ExtendedFHMLViewForm() {
        setStyleClasses("extendedFHMLViewForm");
        setWrapperStyle("extendedFHMLViewFormWrapper");
    }
}
