package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;

import java.util.Map;

@DesignerControl(defaultWidth = 2)
@Control(parents = {PanelGroup.class, Column.class, Tab.class, Row.class, Form.class, Group.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.CHARTS_MAPS, value = "Simple circular chart.", icon = "fa fa-circle-notch")
public class MeterGaugeChart extends FormElement implements Boundable, TableComponent<MeterGaugeChart> {

    private static final String MAX_VALUE_ATTR = "maxValue";
    private static final String MIN_VALUE_ATTR = "minValue";
    private static final String VALUE_ATTR = "value";
    private static final String UNIT_ATTR = "unit";
    private static final String TITLE_ATTR = "title";
    private static final String COLOR_ATTR = "color";
    private static final String FILL_COLOR_ATTR = "fillColor";

    private static final String DEFAULT_COLOR = "#eeeeee";
    private static final String DEFAULT_FILL_COLOR = "#ff0000";
    private static final String DEFAULT_TITLE = "";

    @Getter
    private int maxValue = 100;

    @Getter
    private int minValue = 0;

    @Getter
    private int value;

    @Getter
    private String unit = "%";

    @Getter
    private String title = "";

    @Getter
    private String color = "#eeeeee";

    @Getter
    private String fillColor = "#ff0000";

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = VALUE_ATTR)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    @DocumentedComponentAttribute(boundable = true, value = "Measured quantity")
    private ModelBinding<Integer> valueModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = TITLE_ATTR)
    @DocumentedComponentAttribute(value = "title of chart.")
    private ModelBinding<String> titleModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = COLOR_ATTR, defaultValue = DEFAULT_COLOR)
    @DocumentedComponentAttribute(value = "Chart color")
    private ModelBinding<String> colorModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FILL_COLOR_ATTR, defaultValue = DEFAULT_FILL_COLOR)
    @DocumentedComponentAttribute(value = "Chart fill color")
    private ModelBinding<String> fillColorModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = MIN_VALUE_ATTR)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    @DocumentedComponentAttribute(defaultValue = "0", value = "Minimum quantity of measured value")
    private ModelBinding<Integer> minValueModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = MAX_VALUE_ATTR)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    @DocumentedComponentAttribute(defaultValue = "100", value = "Maximum quantity of measured value")
    private ModelBinding<Integer> maxValueModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = UNIT_ATTR)
    @DocumentedComponentAttribute(defaultValue = "%", value = "Unit of measured value")
    private ModelBinding<String> unitModelBinding;

    public MeterGaugeChart(Form<?> form) {
        super(form);
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();

        if (minValueModelBinding != null) {
            minValue = minValueModelBinding.resolveValueAndAddChanges(this, elementChanges, minValue, MIN_VALUE_ATTR);
        }

        if (maxValueModelBinding != null) {
            maxValue = maxValueModelBinding.resolveValueAndAddChanges(this, elementChanges, maxValue, MAX_VALUE_ATTR);
        }

        if (valueModelBinding != null) {
            value = valueModelBinding.resolveValueAndAddChanges(this, elementChanges, value, VALUE_ATTR);
        }

        if (titleModelBinding != null) {
            title = titleModelBinding.resolveValueAndAddChanges(this, elementChanges, title, TITLE_ATTR);
        } else {
            title = DEFAULT_TITLE;
            elementChanges.addChange(TITLE_ATTR, DEFAULT_TITLE);
        }

        if (colorModelBinding != null) {
            color = colorModelBinding.resolveValueAndAddChanges(this, elementChanges, color, COLOR_ATTR);
        }

        if (fillColorModelBinding != null) {
            fillColor = fillColorModelBinding.resolveValueAndAddChanges(this, elementChanges, fillColor, FILL_COLOR_ATTR);
        }

        if (unitModelBinding != null) {
            unit = unitModelBinding.resolveValueAndAddChanges(this, elementChanges, unit, UNIT_ATTR);
        }

        return elementChanges;
    }

    @Override
    public MeterGaugeChart createNewSameComponent() {
        return new MeterGaugeChart(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, MeterGaugeChart clonedChart) {
        TableComponent.super.doCopy(table, iteratorReplacements, clonedChart);

        clonedChart.setValueModelBinding(table.getRowBinding(this.getValueModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setTitleModelBinding(table.getRowBinding(this.getTitleModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setColorModelBinding(table.getRowBinding(this.getColorModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setFillColorModelBinding(table.getRowBinding(this.getFillColorModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setUnitModelBinding(table.getRowBinding(this.getUnitModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setMaxValueModelBinding(table.getRowBinding(this.getMaxValueModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setMinValueModelBinding(table.getRowBinding(this.getMinValueModelBinding(), clonedChart, iteratorReplacements));
    }
}
