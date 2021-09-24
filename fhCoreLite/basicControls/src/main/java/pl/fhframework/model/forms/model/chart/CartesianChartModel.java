package pl.fhframework.model.forms.model.chart;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Chart;

import java.util.*;

/**
 * Created by k.czajkowski on 23.01.2017.
 */
@Getter
@Setter
public class CartesianChartModel extends ChartModel {

    @Getter
    protected Map<AxisType, Axis> axes;
    @Getter
    private List<ChartSeries> series;

    public CartesianChartModel() {
        series = new LinkedList<>();
        createAxis();
    }

    protected void createAxis() {
        axes = new HashMap<>();
        axes.put(AxisType.X, new LinearAxis());
        axes.put(AxisType.Y, new LinearAxis());
    }

    public void clear() {
        this.series.clear();
    }

    public void addSeries(ChartSeries chartSeries) {
        this.series.add(chartSeries);
    }

    public Axis getAxisType(String axisType) {
        return axes.get(axisType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartesianChartModel)) return false;
        if (!super.equals(o)) return false;
        CartesianChartModel that = (CartesianChartModel) o;

        boolean equals = Objects.equals(axes, that.axes);
        boolean equals1 = Objects.equals(series, that.series);
        return equals && equals1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), axes, series);
    }
}
