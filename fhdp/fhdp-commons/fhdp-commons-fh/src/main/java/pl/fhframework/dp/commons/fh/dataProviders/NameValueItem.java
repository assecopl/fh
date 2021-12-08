package pl.fhframework.dp.commons.fh.dataProviders;

import java.util.Objects;

public class NameValueItem {
    private final String targetValue;
    private final String displayedValue;
    private final String hint;

    public NameValueItem(String targetValue, String displayedValue, String hint) {
        this.targetValue = targetValue;
        this.displayedValue = displayedValue;
        this.hint = hint;
    }

    public NameValueItem(String targetValue, String displayedValue) {
        this(targetValue, displayedValue, null);
    }

    public String getTargetValue() {
        return targetValue;
    }

    public String getDisplayedValue() {
        return displayedValue;
    }

    public String getHint() {
        return hint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameValueItem that = (NameValueItem) o;
        return Objects.equals(targetValue, that.targetValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetValue);
    }

}
