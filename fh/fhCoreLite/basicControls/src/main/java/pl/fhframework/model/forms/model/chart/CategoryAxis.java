package pl.fhframework.model.forms.model.chart;

import lombok.EqualsAndHashCode;

/**
 * Created by k.czajkowski on 23.01.2017.
 */
@EqualsAndHashCode
public class CategoryAxis extends Axis {

    public CategoryAxis() {
    }

    public CategoryAxis(String label) {
        super(label);
    }
}
