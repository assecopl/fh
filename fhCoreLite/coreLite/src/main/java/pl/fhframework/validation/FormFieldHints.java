package pl.fhframework.validation;


import org.springframework.util.StringUtils;

import pl.fhframework.model.PresentationStyleEnum;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import lombok.Getter;

@Getter
public class FormFieldHints {

    private PresentationStyleEnum presentationStyleEnum;

    private List<FieldValidationResult> fieldValidationResults;

    private String hints;

    public FormFieldHints(List<FieldValidationResult> fieldValidationResults) {
        this.fieldValidationResults = Collections.unmodifiableList(fieldValidationResults);
        this.hints = "";
        StringJoiner hintJoiner = new StringJoiner(";");
        for (FieldValidationResult fieldValidationResult : fieldValidationResults) {
            if (presentationStyleEnum == null || presentationStyleEnum.ordinal() < fieldValidationResult.getPresentationStyleEnum().ordinal()) {
                presentationStyleEnum = fieldValidationResult.getPresentationStyleEnum();
            }
            if (!StringUtils.isEmpty(fieldValidationResult.getMessage())) {
                hintJoiner.add(fieldValidationResult.getMessage());
            }
        }
        this.hints = hintJoiner.toString();
    }


    public FormFieldHints(PresentationStyleEnum presentationStyleEnum, String hints) {
        this.presentationStyleEnum = presentationStyleEnum;
        this.hints = hints;
    }
}
