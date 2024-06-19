package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by k.czajkowski on 18.01.2017.
 */
@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MeterGaugeChartElement extends ComponentElement {
    private String numberValue = "60";
    private String minValue = "0";
    private String maxValue = "100";

    private String title = "Sample chart";
    private String unit = "%";
    private String color = "black";
    private String fillColor = "blue";

    private List<ChartValue> sampleNumbers = Arrays.asList(new ChartValue(10), new ChartValue(20));

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ChartValue {
        private int value;
    }
}
