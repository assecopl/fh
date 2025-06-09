package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

@Data
public class ChartTicks {
    private boolean beginAtZero;
    private boolean display = true;
    private ChartFont font;
    private Integer padding = 3;
    private Integer backdropPadding = 3;
    private Integer textStrokeWidth = 0;
    private Integer z = 0;

    // Getters and Setters
}