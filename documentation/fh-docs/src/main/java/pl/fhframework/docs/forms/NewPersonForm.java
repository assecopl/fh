package pl.fhframework.docs.forms;

import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.annotations.AvailabilityRuleMethod;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.AccessibilityRule;
import pl.fhframework.model.forms.Form;

public class NewPersonForm extends Form<Person> {

    @AvailabilityRuleMethod("pSavePerson")
    protected AccessibilityEnum getSavePerson(AccessibilityRule accessibilityRule){
        return AccessibilityEnum.EDIT;
    }
}
