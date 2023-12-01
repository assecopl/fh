package pl.fhframework.fhdp.example.lookup;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Form;


public class LookupForm extends Form<LookupForm.Model> {
    @Getter
    @Setter
    public static class Model {
        private String countryCode;
        private String countryCodeNameValue;
        private String referenceDate;
        private String regionFilter;
    }

    public LookupForm() {
        setStyleClasses("lookupForm");
        setWrapperStyle("lookupFormWrapper");
    }
}
