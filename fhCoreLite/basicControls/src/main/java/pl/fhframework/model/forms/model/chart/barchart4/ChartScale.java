package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

@Data
public class ChartScale {
    private boolean display = true;
    private ChartScaleTitle title;
    private ChartGrid grid;
    private ChartTicks ticks;
    private String type; // np. "linear", "logarithmic", "category"
    private String position; // top, left, bottom, right, etc.
    private String bounds;   // "ticks" | "data"
    private boolean stacked;
    private Integer min;
    private Integer max;

    public ChartScale(){
        grid = new ChartGrid();
        ticks = new ChartTicks();
        title = new ChartScaleTitle();
    }
    // Getters and Setters
}