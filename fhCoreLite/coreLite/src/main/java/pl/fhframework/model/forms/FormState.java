package pl.fhframework.model.forms;

import lombok.Getter;

/**
 * State of a form.
 */
public enum FormState {

    ACTIVE(true),
    INACTIVE_PENDING(true),
    INACTIVE(true),
    SHADOWED(true),
    HIDDEN(false),
    CLOSED(false);

    FormState(boolean displayed) {
        this.displayed = displayed;
    }

    @Getter
    private boolean displayed;

    public static FormState selectMoreRestrictive(FormState state1, FormState state2) {
        if (state1.ordinal() > state2.ordinal()) {
            return state1;
        } else {
            return state2;
        }
    }
}
