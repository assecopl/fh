package pl.fhframework.model.forms.model.chart;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.model.Model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by k.czajkowski on 23.01.2017.
 */
@Getter
@Setter
@EqualsAndHashCode
@Model
public class ChartSeries {

    private String label;
    private Map<Object, Number> data = new LinkedHashMap<>();

    public ChartSeries() {
    }

    public void set(Object x, Number y) {
        data.put(x,y);
    }

    @Override
    public String toString() {
        return "ChartSeries{" +
                "label='" + label + '\'' +
                ", data=" + data +
                '}';
    }
}
