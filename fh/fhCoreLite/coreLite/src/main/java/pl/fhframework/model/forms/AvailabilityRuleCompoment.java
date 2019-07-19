package pl.fhframework.model.forms;

import lombok.Getter;

import java.util.List;

public abstract class AvailabilityRuleCompoment extends FormElement {

    @Getter
    private List<String> idList;

    public AvailabilityRuleCompoment(Form form) {
        super(form);
    }


    @Override
    public void finalizeReading(String text) {
        //TODO: Put here writing all availability rule to form
    }
}
