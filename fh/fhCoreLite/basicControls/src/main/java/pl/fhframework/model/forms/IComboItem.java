package pl.fhframework.model.forms;

/**
 * Created by Piotr on 2017-09-29.
 */
public interface IComboItem {

    public String getTargetValue();

    public default String getDisplayedValue() {
        return getTargetValue();
    }

    public default Integer getTargetCursorPosition() {
        return null;
    }

    public Long getTargetId();

    public String getFullHintWithoutExtras();
}
