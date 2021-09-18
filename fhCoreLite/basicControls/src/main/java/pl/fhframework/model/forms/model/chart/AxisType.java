package pl.fhframework.model.forms.model.chart;

/**
 * Created by k.czajkowski on 23.01.2017.
 */
public enum AxisType {

    X("xaxis"),Y("yaxis"),
    X2("x2axis"),Y2("y2axis");

    private String text;

    AxisType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
