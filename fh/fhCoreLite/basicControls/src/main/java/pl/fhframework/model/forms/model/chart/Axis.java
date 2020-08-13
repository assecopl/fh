package pl.fhframework.model.forms.model.chart;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by k.czajkowski on 23.01.2017.
 */
@Getter
@Setter
//@EqualsAndHashCode
public abstract class Axis implements Serializable{

    private String label = "";
    private Object min;
    private Object max;

    public Axis() {
    }

    public Axis(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Axis{" +
                "label='" + label + '\'' +
                ", min=" + min +
                ", max=" + max +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Axis)) return false;
        Axis axis = (Axis) o;
        return Objects.equals(label, axis.label) &&
                Objects.equals(min, axis.min) &&
                Objects.equals(max, axis.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, min, max);
    }
}
