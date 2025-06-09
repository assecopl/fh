package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

@Data
public class ChartGrid {
    private boolean display = true;
    private String color;
    private boolean drawOnChartArea = true;
    private boolean drawTicks = true;
    private Integer lineWidth = 1;
    private Integer tickLength = 8;
    private Integer z = -1;
    private boolean offset = false;

    // Getters and Setters
}