package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

import java.util.Map;

@Data
public class ChartOptions {
    private Boolean responsive = true;
    private Boolean maintainAspectRatio = true;
    private Integer aspectRatio = 2;
    private ChartLayout layout;
    private ChartPlugins plugins;
    private Map<String, ChartScale> scales;
    private ChartInteraction interaction;

    public ChartOptions(){
        layout = new ChartLayout();
        plugins = new ChartPlugins();
        interaction = new ChartInteraction();
    }
    // Getters and Setters
}