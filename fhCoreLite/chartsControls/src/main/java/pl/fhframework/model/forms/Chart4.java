package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.model.chart.Axis;
import pl.fhframework.model.forms.model.chart.AxisType;
import pl.fhframework.model.forms.model.chart.BarChartModel;
import pl.fhframework.model.forms.model.chart.ChartSeries;
import pl.fhframework.model.forms.model.chart.barchart4.ChartConfiguration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by k.czajkowski on 23.01.2017.
 */
@Control(parents = {PanelGroup.class, Tab.class, Row.class, Form.class, Group.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.CHARTS_MAPS, documentationExample = true, value = "Bar Chart displaying series of data", icon = "fa fa-chart-bar")
@DesignerControl(defaultWidth = 6)
public class Chart4 extends FormElement implements Boundable, IChangeableByClient {

    private static final String ATTR_VALUES = "configuration";

    @Getter
    private ChartConfiguration configuration;

    @Getter
    private boolean stacked;

    @Getter
    private String colors;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_VALUES)
    @DocumentedComponentAttribute(boundable = true, value = "Chart Configuration - full configuration for Chart.js 4.4.x ")
    private ModelBinding<ChartConfiguration> configurationModelBinding;


    public Chart4(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
    }


    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();

        if (configurationModelBinding != null) {
            configuration = configurationModelBinding.resolveValueAndAddChanges(this, elementChanges, configuration, ATTR_VALUES);
        }

        return elementChanges;
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        super.updateView();
    }
}
