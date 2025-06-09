package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

@Data
public class ChartLegend {
    private boolean display = true;
    private String position = "top";
    private String align = "center";
    private ChartLegendLabels labels;

    // Getters and Setters
}