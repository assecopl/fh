package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

@Data
public class ChartLayout {
    private ChartPadding padding;
    private boolean autoPadding = true;

    public ChartLayout(){
        padding = new ChartPadding();
    }
    // Getters and Setters
}