package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;

/**
 * Created by k.czajkowski on 23.01.2017.
 */
public abstract class Chart extends FormElement implements Boundable, IChangeableByClient {

    protected static final String ATTR_X_AXIS_LABEL = "xAxisLabel";
    protected static final String ATTR_Y_AXIS_LABEL = "yAxisLabel";
    protected static final String ATTR_Y_AXIS_MIN = "yAxisMin";
    protected static final String ATTR_Y_AXIS_MAX = "yAxisMax";
    protected static final String ATTR_TITLE = "title";

    protected static final String X = "X";
    protected static final String Y = "Y";
    public static final String EMPTY_STRING = "";

    @Getter
    private Number yAxisMin;

    @Getter
    private Number yAxisMax;

    @Getter
    private String xAxisLabel = EMPTY_STRING;

    @Getter
    private String yAxisLabel = EMPTY_STRING;

    @Getter
    private String title = EMPTY_STRING;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_X_AXIS_LABEL)
    @DocumentedComponentAttribute(boundable = true, value = "Label of X axis")
    private ModelBinding<String> axisXLabelModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_Y_AXIS_LABEL)
    @DocumentedComponentAttribute(boundable = true, value = "Label of Y axis")
    private ModelBinding<String> axisYLabelModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_Y_AXIS_MIN)
    @DesignerXMLProperty(allowedTypes = Number.class)
    @DocumentedComponentAttribute(boundable = true, value = "Min value on Y axis")
    private ModelBinding<Number> axisYMinModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_Y_AXIS_MAX)
    @DesignerXMLProperty(allowedTypes = Number.class)
    @DocumentedComponentAttribute(boundable = true, value = "Max value of Y axis")
    private ModelBinding<Number> axisYMaxModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(ATTR_TITLE)
    @DocumentedComponentAttribute(boundable = true, value = "Title of a chart")
    private ModelBinding<String> titleModelBinding;

    public Chart(Form form) {
        super(form);
    }

    protected Number getValue(ModelBinding modelBinding) {
        if (modelBinding == null) {
            return null;
        }
        BindingResult bindingResult = modelBinding.getBindingResult();
        if (bindingResult != null) {
            Object value = bindingResult.getValue();
            if (value instanceof Number) {
                return (Number) value;
            } else if (value instanceof String) {
                return Double.valueOf((String) value);
            }
        }
        return 0;
    }

    protected String getStringValue(ModelBinding modelBinding) {
        if (modelBinding == null) {
            return null;
        }
        BindingResult bindingResult = modelBinding.getBindingResult();
        if (bindingResult == null) {
            return null;
        }
        return (String) bindingResult.getValue();
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        super.updateView();
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        updateValues(elementChanges);
        return elementChanges;
    }

    protected void updateValues(ElementChanges elementChanges) {
        this.yAxisMin = processNumberValueBinding(elementChanges, ATTR_Y_AXIS_MIN, getYAxisMin(), getAxisYMinModelBinding());
        this.yAxisMax = processNumberValueBinding(elementChanges, ATTR_Y_AXIS_MAX, getYAxisMax(), getAxisYMaxModelBinding());
        this.yAxisLabel = processStringValueBinding(elementChanges, ATTR_Y_AXIS_LABEL, getYAxisLabel(), getAxisYLabelModelBinding());
        this.xAxisLabel = processStringValueBinding(elementChanges, ATTR_X_AXIS_LABEL, getXAxisLabel(), getAxisXLabelModelBinding());
        this.title = processStringValueBinding(elementChanges, ATTR_TITLE, getTitle(), getTitleModelBinding());
    }

    protected Number processNumberValueBinding(ElementChanges elementChanges, String attributeName, Number oldValue, ModelBinding<Number> modelBinding) {
        if (modelBinding != null) {
            BindingResult<Number> bindingResult = modelBinding.getBindingResult();
            if (bindingResult != null) {
                Number newValue = bindingResult.getValue();
                if (!areValuesTheSame(newValue, oldValue)) {
                    elementChanges.addChange(attributeName, newValue);
                }
                return newValue;
            }
        }
        return oldValue;
    }

    protected String processStringValueBinding(ElementChanges elementChanges, String attributeName, String oldValue, ModelBinding modelBinding) {
        if (modelBinding != null) {
            BindingResult bindingResult = modelBinding.getBindingResult();
            if (bindingResult != null) {
                String newValue = this.convertBindingValueToString(bindingResult);
                if (!areValuesTheSame(newValue, oldValue)) {
                    elementChanges.addChange(attributeName, newValue);
                }
                return newValue;
            }
        }
        return oldValue;
    }
}