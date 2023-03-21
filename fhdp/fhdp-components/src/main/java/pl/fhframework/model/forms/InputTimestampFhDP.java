package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.time.LocalDateTime;
import java.util.Date;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02/06/2020
 */
@TemplateControl(tagName = "fh-input-timestamp")
@DesignerControl(defaultWidth = 3)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, value = "Component responsible for displaying field, where use can set date and time.", icon = "fa fa-clock")
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@OverridenPropertyAnnotations(
        property = "modelBinding",
        designerXmlProperty = @DesignerXMLProperty(allowedTypes = {Date.class, LocalDateTime.class}, commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT))
public class InputTimestampFhDP extends InputTimestamp{
    public static final String ATTR_LAST_VALUE = "lastValue";
    public static final String ATTR_HIDE_CROSSED = "hideCrossed";
    public static final String ATTR_NEW_VALUE_TEXT = "newValueText";
    public static final String ATTR_IS_LAST_VALUE_ENABLED = "isLastValueEnabled";
    public static final String ATTR_ADDITIONAL_BUTTONS = "isAdditionalButtons";
    public static final String ATTR_AVAILABLE_TIME_RANGE = "availableTimeRange";
    public static final String ATTR_CALENDAR_POSITION_VERTICAL = "calendarPositionVertical";
    public static final String ATTR_CALENDAR_POSITION_HORIZONTAL = "calendarPositionHorizontal";

    @Getter
    private String lastValue;

    @Getter
    private String hideCrossed;

    @Getter
    private String newValueText;

    @Getter
    private Boolean isLastValueEnabled;

    @Getter
    private Boolean isAdditionalButtons;

    @Getter
    private String availableTimeRange;

    @Getter
    private String calendarPositionVertical;

    @Getter
    private String calendarPositionHorizontal;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LAST_VALUE) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding lastValueModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_AVAILABLE_TIME_RANGE) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, defaultValue = "FULLTIME",value = "Available time range. Available values: FULLTIME, FUTURETIME, BESTTIME")
    private ModelBinding availableTimeRangeModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_IS_LAST_VALUE_ENABLED) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Display only code, there will be no method invoke from provider.")
    private ModelBinding<Boolean> isLastValueEnabledModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_NEW_VALUE_TEXT) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding<String> newValueTextModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_HIDE_CROSSED) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding hideCrossedModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_CALENDAR_POSITION_VERTICAL) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(defaultValue = "auto", boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding calendarPositionVerticalModelBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_CALENDAR_POSITION_HORIZONTAL) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(defaultValue = "auto", boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding calendarPositionHorizontalModelBinding;


    public InputTimestampFhDP(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();

        if (newValueText != null) {
            BindingResult bidingResult = newValueTextModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.newValueText = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }
        if (lastValue != null) {
            BindingResult bidingResult = lastValueModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.lastValue = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }
        if (availableTimeRange != null) {
            BindingResult bidingResult = availableTimeRangeModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.availableTimeRange = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }
        if (isLastValueEnabled != null) {
            BindingResult bidingResult = isLastValueEnabledModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.isLastValueEnabled = convertValue(bidingResult.getValue(), Boolean.class);
                }
            }
        }

        if(hideCrossed != null){
            BindingResult bindingResult = hideCrossedModelBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.hideCrossed = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

        if(calendarPositionVertical != null){
            BindingResult bindingResult = calendarPositionVerticalModelBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.calendarPositionVertical = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

        if(calendarPositionHorizontal != null){
            BindingResult bindingResult = calendarPositionHorizontalModelBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.calendarPositionHorizontal = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

    }
    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if(newValueTextModelBinding != null) {
            BindingResult bindingResult = newValueTextModelBinding.getBindingResult();
            this.newValueText = convertValue(bindingResult.getValue(), String.class);
        }
        if(availableTimeRangeModelBinding != null) {
            BindingResult bindingResult = availableTimeRangeModelBinding.getBindingResult();
            this.availableTimeRange = convertValue(bindingResult.getValue(), String.class);
        }
        if(isLastValueEnabledModelBinding != null) {
            BindingResult bindingResult = isLastValueEnabledModelBinding.getBindingResult();
            this.isLastValueEnabled = convertValue(bindingResult.getValue(), Boolean.class);
        }
        if (lastValueModelBinding != null) {
            BindingResult bindingResult = lastValueModelBinding.getBindingResult();

            String newRaw = convertToRaw(bindingResult);
            this.lastValue = newRaw;
            elementChange.addChange(ATTR_LAST_VALUE, this.lastValue);
        }

        if (hideCrossedModelBinding != null) {
            BindingResult bindingResult = hideCrossedModelBinding.getBindingResult();

            String newRaw = bindingResult.getValue().toString();

            this.hideCrossed = newRaw;
            elementChange.addChange(ATTR_HIDE_CROSSED, this.hideCrossed);

        }
        if(calendarPositionVerticalModelBinding != null) {
            BindingResult bindingResult = calendarPositionVerticalModelBinding.getBindingResult();
            this.calendarPositionVertical = convertValue(bindingResult.getValue(), String.class);
        }
        if(calendarPositionHorizontalModelBinding != null) {
            BindingResult bindingResult = calendarPositionHorizontalModelBinding.getBindingResult();
            this.calendarPositionHorizontal = convertValue(bindingResult.getValue(), String.class);
        }
        return elementChange;
    }
}
