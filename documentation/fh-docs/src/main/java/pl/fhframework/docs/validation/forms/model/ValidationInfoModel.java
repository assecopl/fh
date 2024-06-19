package pl.fhframework.docs.validation.forms.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ValidationInfoModel {

    @NotEmpty
    private String name;

    @Size(min = 3, max = 5, message = "Length 3-5 required")
    private String surname;

    private String validationRuleValue;

    private String dontValidateName;
    private String nonEditableField;
    private String hiddenField;
    private String eventName;
    private String eventSurname;
    private int activeTabId;

    private ValidationPersonModel firstPerson = new ValidationPersonModel();

    private ValidationPersonModel secondPerson = new ValidationPersonModel();

    private List<ValidationPersonModel> personList = Arrays.asList(
            new ValidationPersonModel("John Doe", null),
            new ValidationPersonModel("Tom Black", null)
    );
}
