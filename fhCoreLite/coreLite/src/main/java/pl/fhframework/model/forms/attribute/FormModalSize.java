package pl.fhframework.model.forms.attribute;

/**
 * Type of a form
 */
public enum FormModalSize {

    SMALL, REGULAR, LARGE, XLARGE, XXLARGE, FULL;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
