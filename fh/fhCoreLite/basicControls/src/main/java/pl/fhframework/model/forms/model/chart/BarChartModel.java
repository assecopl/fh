package pl.fhframework.model.forms.model.chart;

import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by k.czajkowski on 23.01.2017.
 */
public class BarChartModel extends CartesianChartModel {

    @Override
    protected void createAxis() {
        axes = new HashMap<AxisType, Axis>();
        axes.put(AxisType.X, new CategoryAxis());
        axes.put(AxisType.Y, new LinearAxis());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
