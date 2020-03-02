package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.model.chart.Axis;
import pl.fhframework.model.forms.model.chart.AxisType;
import pl.fhframework.model.forms.model.chart.BarChartModel;
import pl.fhframework.model.forms.model.chart.ChartSeries;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by k.czajkowski on 23.01.2017.
 */
@Control(parents = {PanelGroup.class, Tab.class, Row.class, Form.class, Group.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.CHARTS_MAPS, value = "Bar Chart displaying series of data", icon = "fa fa-chart-bar")
@DesignerControl(defaultWidth = 6)
public class BarChart extends Chart {

    private static final String ATTR_VALUES = "values";
    protected static final String ATTR_STACKED = "stacked";
    protected static final String ATTR_COLORS = "colors";

    @JsonIgnore
    @Getter
    private List<ChartSeries> values;

    @Getter
    private BarChartModel barChartModel;

    @Getter
    private boolean stacked;

    @Getter
    private String colors;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_VALUES)
    @DesignerXMLProperty(allowedTypes = List.class)
    @DocumentedComponentAttribute(boundable = true, value = "List<ChartSeries> object representing series of data. First ChartSeries of data has to contain all values from X series. ")
    private ModelBinding valuesModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_STACKED)
    @DesignerXMLProperty(allowedTypes = boolean.class)
    @DocumentedComponentAttribute(boundable = true, defaultValue = "false", value = "Determines if series of data should be stacked or not")
    private ModelBinding stackedModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_COLORS)
    @DocumentedComponentAttribute(boundable = true, value = "Defines colors of data series, values are separated with \"|\". For example colors=&quot;black,blue,yellow&quot; says that first series of data is black, second is blue, third is yellow.")
    private ModelBinding colorsModelBinding;

    public BarChart(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        this.barChartModel = new BarChartModel();

        if (colorsModelBinding != null) {
            BindingResult bindingResult = colorsModelBinding.getBindingResult();
            if (bindingResult != null) {
                colors = (String) bindingResult.getValue();
            }
        }

        if (stackedModelBinding != null) {
            BindingResult bindingResult = stackedModelBinding.getBindingResult();
            if (bindingResult != null) {
                Object value = bindingResult.getValue();
                if (value instanceof String) {
                    stacked = Boolean.parseBoolean((String) value);
                } else if (value instanceof Boolean) {
                    stacked = (boolean) value;
                }
            }
        }
    }

    private BarChartModel initBarChartModel(BarChartModel barChartModel, List<ChartSeries> values) {
        barChartModel.setTitle(getTitle());
        updateAxes(barChartModel);
        addDataSeries(barChartModel, values);
        return barChartModel;
    }

    private void addDataSeries(BarChartModel barChartModel, List<ChartSeries> valuesList) {
        if (valuesList == null) {
            FhLogger.debug(this.getClass(), logger -> logger.log("Data series for BarChart are not initialized!"));
            return;
        }
        for (ChartSeries chartSeries : valuesList) {
            ChartSeries series = new ChartSeries();
            series.setLabel(chartSeries.getLabel());
            series.setData(new LinkedHashMap<>(chartSeries.getData()));
            barChartModel.addSeries(series);
        }
    }

    private void updateAxes(BarChartModel barChartModel) {
        Map<AxisType, Axis> axes = barChartModel.getAxes();
        Axis xAxis = axes.get(AxisType.X);
        xAxis.setLabel(getXAxisLabel());
        Axis yAxis = axes.get(AxisType.Y);
        yAxis.setLabel(getYAxisLabel());
        yAxis.setMin(this.getYAxisMin());
        yAxis.setMax(this.getYAxisMax());
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        super.updateModel(valueChange);
        this.updateBinding(valueChange, valuesModelBinding, values);
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        if (stackedModelBinding != null) {
            BindingResult stackedBindingResult = stackedModelBinding.getBindingResult();
            if (stackedBindingResult != null) {
                boolean newStacked = this.stacked;
                Object value = stackedBindingResult.getValue();
                if (value instanceof String) {
                    newStacked = Boolean.valueOf((String) value);
                } else if (value instanceof Boolean) {
                    newStacked = (boolean) value;
                }
                if (!Objects.equals(newStacked, this.stacked)) {
                    this.stacked = newStacked;
                    elementChanges.addChange(ATTR_STACKED, this.stacked);
                }
            }
        }
        if (colorsModelBinding != null) {
            BindingResult colorsBindingResult = colorsModelBinding.getBindingResult();
            if (colorsBindingResult != null) {
                String newColors = (String) colorsBindingResult.getValue();
                if (!Objects.equals(newColors, this.colors)) {
                    this.colors = newColors;
                    elementChanges.addChange(ATTR_COLORS, this.colors);
                }
            }
        }
        if (valuesModelBinding != null) {
            BindingResult bindingResult = valuesModelBinding.getBindingResult();
            if (bindingResult != null) {
                Object value = bindingResult.getValue();
                BarChartModel newBarChartModel = new BarChartModel();
                newBarChartModel = initBarChartModel(newBarChartModel, (List<ChartSeries>) value);
                newBarChartModel.setTitle(getTitle());
                if (!Objects.equals(barChartModel.getSeries(), newBarChartModel.getSeries()) || newBarChartModel.getSeries().isEmpty()) {
                    values = (List<ChartSeries>) value;
                    this.barChartModel.clear();
                    addDataSeries(this.barChartModel, this.values);
                    FhLogger.debug(this.getClass(), logger -> logger.log("Updated values: " + values));
                    elementChanges.addChange(ATTR_VALUES, this.values);
                }
            }
        }
        updateAxes(this.barChartModel);
        return elementChanges;
    }
}
