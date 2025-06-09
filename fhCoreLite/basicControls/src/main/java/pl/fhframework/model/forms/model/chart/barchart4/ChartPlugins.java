package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

@Data
public class ChartPlugins {
    private ChartTitle title;
    private ChartLegend legend;
    private ChartTooltip tooltip;

    public ChartPlugins(){
        title = new ChartTitle();
        legend = new ChartLegend();
        tooltip = new ChartTooltip();
    }

    // Getters and Setters
}