package pl.fhframework.model.forms.model.chart.barchart4;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
public class ChartConfiguration {
    private String type = "line";
    private ChartData data;
    private ChartOptions options;
    private List<Object> plugins;

    public ChartConfiguration(){
        this.options = new ChartOptions();
        this.plugins = new ArrayList<>();
        this.data = new ChartData();
        this.data.setLabels(new ArrayList<>());
        this.data.setDatasets(new ArrayList<>());
    }
    // Getters and Setters
}