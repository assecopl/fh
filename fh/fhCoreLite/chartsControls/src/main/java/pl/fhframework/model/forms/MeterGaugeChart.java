package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.DesignerModelBinding;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;

import java.util.Map;
import java.util.Optional;

/**
 * Created by k.czajkowski on 13.01.2017.
 */
@DesignerControl(defaultWidth = 2)
@Control(parents = {PanelGroup.class, Column.class, Tab.class, Row.class, Form.class, Group.class}, canBeDesigned = true)
@DocumentedComponent(value = "Simple circular chart.", icon = "fa fa-circle-notch")
public class MeterGaugeChart extends FormElement implements IChangeableByClient, Boundable, TableComponent<MeterGaugeChart> {

    private static final String MAX_VALUE_ATTR = "maxValue";
    private static final String MIN_VALUE_ATTR = "minValue";
    private static final String VALUE_ATTR = "value";
    private static final String PERCENTAGE_ATTR = "percentage";
    private static final String UNIT_ATTR = "unit";
    private static final String TITLE_ATTR = "title";
    private static final String COLOR_ATTR = "color";
    private static final String FILL_COLOR_ATTR = "fillColor";

    private static final String DEFAULT_UNIT = "%";
    private static final String DEFAULT_TITLE = "";
    private static final String DEFAULT_COLOR = "#eeeeee";
    private static final String DEFAULT_FILL_COLOR = "#ff0000";

    @Getter
    private int maxValue = 100;

    @Getter
    private int minValue;

    @Getter
    private int value;

    @Getter
    @Setter
    @XMLProperty
    private int percentage;

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
    private ModelBinding valueModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = TITLE_ATTR)
    @DocumentedComponentAttribute(value = "title of chart.")
    private ModelBinding titleModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = COLOR_ATTR, defaultValue = DEFAULT_COLOR)
    @DocumentedComponentAttribute(value = "Chart color")
    private ModelBinding colorModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FILL_COLOR_ATTR, defaultValue = DEFAULT_FILL_COLOR)
    @DocumentedComponentAttribute(value = "Chart fill color")
    private ModelBinding fillColorModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = MIN_VALUE_ATTR)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    @DocumentedComponentAttribute(defaultValue = "0", value = "Minimum quantity of measured value")
    private ModelBinding minValueModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = MAX_VALUE_ATTR)
    @DesignerXMLProperty(allowedTypes = Integer.class)
    @DocumentedComponentAttribute(defaultValue = "100", value = "Maximum quantity of measured value")
    private ModelBinding maxValueModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = UNIT_ATTR)
    @DocumentedComponentAttribute(defaultValue = "%", value = "Unit of measured value")
    private ModelBinding unitModelBinding;

    public MeterGaugeChart(Form form) {
        super(form);
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        String newValue = valueChange.getMainValue();
        int changedValue = Integer.parseInt(newValue);
        if (changedValue != this.value) {
            this.value = changedValue;
        }
        this.updateBinding(valueChange, valueModelBinding, this.value);
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        boolean refreshView = updateBoundableValues(elementChanges);
        if (refreshView) {
            refreshView();
        }
        return elementChanges;
    }

    private boolean updateBoundableValues(ElementChanges elementChanges) {
        boolean refreshView = false;
        int oldValue = 0;
        int newValue = 0;

        oldValue = this.minValue;
        newValue = processIntegerValueBinding(elementChanges, MIN_VALUE_ATTR, this.minValue, minValueModelBinding);
        if (oldValue != newValue) {
            this.minValue = newValue;
            refreshView = true;
        }

        oldValue = this.maxValue;
        newValue = processIntegerValueBinding(elementChanges, MAX_VALUE_ATTR, this.maxValue, maxValueModelBinding);
        if (newValue < this.minValue) {
            newValue = this.minValue;
            this.maxValue = newValue;
        }
        if (oldValue != newValue) {
            this.maxValue = newValue;
            refreshView = true;
        }

        newValue = processIntegerValueBinding(elementChanges, VALUE_ATTR, this.value, valueModelBinding);
        if (newValue >= this.maxValue) {
            newValue = this.maxValue;
            this.value = newValue;
        }
        oldValue = this.value;
        if (oldValue != newValue) {
            this.value = newValue;
            refreshView = true;
        }

        int percentageValue = calculatePercentage();
        if (percentageValue != this.percentage) {
            this.percentage = percentageValue;
            elementChanges.addChange(PERCENTAGE_ATTR, this.percentage);
            refreshView = true;
        }

        String newTitle = processStringValueBinding(elementChanges, TITLE_ATTR, title, titleModelBinding);
        if (!this.title.equals(newTitle)) {
            refreshView = true;
            this.title = newTitle;
        }

        String newColor = processStringValueBinding(elementChanges, COLOR_ATTR, color, colorModelBinding);
        if (!this.color.equals(newColor)) {
            refreshView = true;
            this.color = newColor;
        }

        String newFillColor = processStringValueBinding(elementChanges, FILL_COLOR_ATTR, fillColor, fillColorModelBinding);
        if (!this.fillColor.equals(newFillColor)) {
            refreshView = true;
            this.fillColor = newFillColor;
        }

        String newUnit = processStringValueBinding(elementChanges, UNIT_ATTR, unit, unitModelBinding);
        if (!this.unit.equals(newUnit)) {
            refreshView = true;
            this.unit = newUnit;
        }
        return refreshView;
    }

    private int calculatePercentage() {
        if (this.value < this.minValue) {
            return 0;
        }
        int denominator = maxValue - minValue;
        if (denominator == 0) {
            return 100;
        }
        if (this.value > this.maxValue) {
            return 100;
        }
        double fraction = (value - minValue) * 1.0 / denominator;
        return (int) Math.round((fraction * 100));
    }

    protected String processStringValueBinding(ElementChanges elementChanges, String attributeName, String oldValue, ModelBinding modelBinding) {
        String newValue = oldValue;
        if (modelBinding != null) {
            BindingResult bindingResult = modelBinding.getBindingResult();
            if (bindingResult != null) {

                String newStringValue = "";
                if (modelBinding instanceof DesignerModelBinding) {
                    Optional<String> staticValueText = Optional.ofNullable(((DesignerModelBinding) modelBinding).getStaticValueText());
                    newStringValue = staticValueText.orElse("");
                } else {
                    newStringValue = this.convertBindingValueToString(bindingResult);
                }
                newValue = newStringValue;
                if (!areValuesTheSame(newValue, oldValue)) {
                    elementChanges.addChange(attributeName, newValue);
                }
            }
        }
        return newValue;
    }

    protected int processIntegerValueBinding(ElementChanges elementChanges, String attributeName, int oldValue, ModelBinding modelBinding) {
        int newValue = oldValue;
        if (modelBinding != null) {
            BindingResult bindingResult = modelBinding.getBindingResult();
            if (bindingResult != null) {
                String newStringValue;
                if (modelBinding instanceof DesignerModelBinding) {
                    newStringValue = ((DesignerModelBinding) modelBinding).getStaticValueText();
                } else {
                    newStringValue = this.convertBindingValueToString(bindingResult);
                }
                try {
                    newValue = Integer.parseInt(newStringValue);
                } catch (NumberFormatException e) {
                    //do not show error message, only log it
                    FhLogger.debug(this.getClass(), logger -> logger.log("Could not parse int: {}", newStringValue));
                }
                if (!areValuesTheSame(newValue, oldValue)) {
                    elementChanges.addChange(attributeName, newValue);
                }
            }
        }
        return newValue;
    }

    @Override
    public MeterGaugeChart createNewSameComponent() {
        return new MeterGaugeChart(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, MeterGaugeChart clonedChart) {
        TableComponent.super.doCopy(table, iteratorReplacements, clonedChart);

        clonedChart.setPercentage(getPercentage());
        clonedChart.setValueModelBinding(table.getRowBinding(this.getValueModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setTitleModelBinding(table.getRowBinding(this.getTitleModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setColorModelBinding(table.getRowBinding(this.getColorModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setFillColorModelBinding(table.getRowBinding(this.getFillColorModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setUnitModelBinding(table.getRowBinding(this.getUnitModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setMaxValueModelBinding(table.getRowBinding(this.getMaxValueModelBinding(), clonedChart, iteratorReplacements));
        clonedChart.setMinValueModelBinding(table.getRowBinding(this.getMinValueModelBinding(), clonedChart, iteratorReplacements));
    }
}