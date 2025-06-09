package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

@Data
public class ChartTooltip {
    private boolean enabled = true;
    private String mode;
    private boolean intersect;

    // Getters and Setters
}