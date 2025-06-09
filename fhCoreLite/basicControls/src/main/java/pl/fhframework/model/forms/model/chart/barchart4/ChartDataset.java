package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

import java.util.List;

@Data
public class ChartDataset {
    private String label;
    private List<Number> data;
    private Object backgroundColor;
    private Object borderColor;
    private Integer borderWidth;
    private String stack;

    // Getters and Setters
}