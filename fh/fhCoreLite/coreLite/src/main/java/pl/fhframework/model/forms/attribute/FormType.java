package pl.fhframework.model.forms.attribute;

/**
 * Type of a form
 */
public enum FormType {

    STANDARD, MODAL, MODAL_OVERFLOW, FLOATING, HEADER, MINIMAL;
    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public boolean isModal() {
        return this == MODAL || this == MODAL_OVERFLOW;
    }
}
