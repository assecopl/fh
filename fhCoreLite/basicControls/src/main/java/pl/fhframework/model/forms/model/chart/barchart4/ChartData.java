package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;

import java.util.List;

@Data
public class ChartData {
    private List<String> labels;
    private List<ChartDataset> datasets;

    // Getters and Setters
}