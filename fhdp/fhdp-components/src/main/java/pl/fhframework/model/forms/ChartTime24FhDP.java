package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;
import pl.fhframework.model.forms.widgets.Widget;

import java.util.List;

@Getter
@Setter
@DesignerControl(defaultWidth = 1)
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class, PanelHeaderFhDP.class, PanelFhDP.class}, invalidParents = {Table.class, Widget.class, Repeater.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.IMAGE_HTML_MD, value = "Show data on charts", icon = "fa fa-eye")
public class ChartTime24FhDP extends FormElement {

    // base parameters
    public static final String ATTR_V_AXIS_NAME = "vAxisName";
    public static final String ATTR_H_AXIS_NAME = "hAxisName";
    public static final String ATTR_BG_COLOR = "bgColor";
    public static final String ATTR_TOOLTIP_BG_COLOR = "tooltipBgColor";
    public static final String ATTR_AXIS_LABEL_COLOR = "axisLabelColor";
    public static final String ATTR_LINE_COLOR = "lineColor";
    public static final String ATTR_TOOLTIP_COLOR = "tooltipColor";
    public static final String ATTR_FONT_FAMILY = "fontFamily";
    public static final String ATTR_BARS = "bars";
    public static final String ATTR_HIGHLIGHTED_VALUE = "highlightedValue";
    public static final String ATTR_HIGHLIGHTED_STROKE = "highlightedStroke";
    public static final String ATTR_GRADATION = "gradation";
    public static final String ATTR_MAX_VALUE_X = "maxValueX";

    // container ids
    public static final String ATTR_MAIN_ELEMENT_ID = "mainElementId";
    public static final String ATTR_SCROLL_CONTAINER_ID = "scrollContainerId";

    // bar
    public static final String ATTR_BAR_BREAK_WIDTH = "barBreakWidth";
    public static final String ATTR_BAR_HEIGHT = "barHeight";
    public static final String ATTR_BAR_BREAK_HEIGHT = "barBreakHeight";
    public static final String ATTR_PERCENT_DARK_COLOR_ON_MOVE = "percentDarkColorOnMove";

    // base margin
    public static final String ATTR_LEFT_MARGIN_CHART = "leftMarginChart";
    public static final String ATTR_RIGHT_MARGIN_CHART = "rightMarginChart";
    public static final String ATTR_TOP_MARGIN_CHART = "topMarginChart";
    public static final String ATTR_BOTTOM_MARGIN_CHART = "bottomMarginChart";

    // scroll container
    public static final String ATTR_SCROLL_SIZE_CHART = "scrollSizeChart";

    // lines, value lines
    public static final String ATTR_PROTRUDING_LINES_HEIGHT = "protrudingLinesHeight";
    public static final String ATTR_LABEL_TEXT_HEIGHT = "labelTextHeight";
    public static final String ATTR_LABEL_TEXT_FONT_SIZE = "labelTextFontSize";
    public static final String ATTR_LINE_STROKE_WIDTH = "lineStrokeWidth";

    // legend
    public static final String ATTR_TOP_MARGIN_LEGEND = "topMarginLegend";
    public static final String ATTR_LEGEND_COLOR_WIDTH = "legendColorWidth";
    public static final String ATTR_LEGEND_COLOR_HEIGHT = "legendColorHeight";
    public static final String ATTR_LEGEND_FONT_SIZE = "legendFontSize";
    public static final String ATTR_LEGEND_BREAK_COLOR_TEXT = "legendBreakColorText";
    public static final String ATTR_LEGEND_ELEMENT_BREAK_WIDTH = "legendElementBreakWidth";
    public static final String ATTR_LEGEND_ELEMENT_BREAK_HEIGHT = "legendElementBreakHeight";

    // tooltip
    public static final String ATTR_TOOLTIP_OPACITY = "tooltipOpacity";
    public static final String ATTR_TOOLTIP_POINTER_WIDTH = "tooltipPointerWidth";
    public static final String ATTR_TOOLTIP_POINTER_HEIGHT = "tooltipPointerHeight";
    public static final String ATTR_TOOLTIP_CORNER_RADIUS = "tooltipCornerRadius";
    public static final String ATTR_TOOLTIP_FONT_SIZE = "tooltipFontSize";
    public static final String ATTR_TOOLTIP_PADDING = "tooltipPadding";

    // label name prop
    public static final String ATTR_LABEL_NAME_FONT_SIZE = "labelNameFontSize";
    public static final String ATTR_LABEL_NAME_BREAK = "labelNameBreak";

    // base parameters
    @Getter
    private String vAxisName;
    @Getter
    private String hAxisName;
    @Getter
    private String bgColor;
    @Getter
    private String tooltipBgColor;
    @Getter
    private String axisLabelColor;
    @Getter
    private String lineColor;
    @Getter
    private String tooltipColor;
    @Getter
    private String fontFamily;
    @Getter
    private List bars;
    @Getter
    private String highlightedValue;
    @Getter
    private String highlightedStroke;
    @Getter
    private String gradation;
    @Getter
    private String maxValueX;

    // container ids
    @Getter
    private String mainElementId;
    @Getter
    private String scrollContainerId;

    // bar
    @Getter
    private String barBreakWidth;
    @Getter
    private String barHeight;
    @Getter
    private String barBreakHeight;
    @Getter
    private String percentDarkColorOnMove;

    // base margin
    @Getter
    private String leftMarginChart;
    @Getter
    private String rightMarginChart;
    @Getter
    private String topMarginChart;
    @Getter
    private String bottomMarginChart;

    // scroll container
    @Getter
    private String scrollSizeChart;

    // lines, value lines
    @Getter
    private String protrudingLinesHeight;
    @Getter
    private String labelTextHeight;
    @Getter
    private String labelTextFontSize;
    @Getter
    private String lineStrokeWidth;

    // legend
    @Getter
    private String topMarginLegend;
    @Getter
    private String legendColorWidth;
    @Getter
    private String legendColorHeight;
    @Getter
    private String legendFontSize;
    @Getter
    private String legendBreakColorText;
    @Getter
    private String legendElementBreakWidth;
    @Getter
    private String legendElementBreakHeight;

    // tooltip
    @Getter
    private String tooltipOpacity;
    @Getter
    private String tooltipPointerWidth;
    @Getter
    private String tooltipPointerHeight;
    @Getter
    private String tooltipCornerRadius;
    @Getter
    private String tooltipFontSize;
    @Getter
    private String tooltipPadding;

    // label name prop
    @Getter
    private String labelNameFontSize;
    @Getter
    private String labelNameBreak;

    // container ids
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_V_AXIS_NAME)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> vAxisNameBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_H_AXIS_NAME)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> hAxisNameBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_BG_COLOR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> bgColorBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_TOOLTIP_BG_COLOR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> tooltipBgColorBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_AXIS_LABEL_COLOR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> axisLabelColorBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_LINE_COLOR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> lineColorBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_TOOLTIP_COLOR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> tooltipColorBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_FONT_FAMILY)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> fontFamilyBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_BARS)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<List> barsBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_HIGHLIGHTED_VALUE)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> highlightedValueBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_HIGHLIGHTED_STROKE)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> highlightedStrokeBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_GRADATION)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> gradationBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_MAX_VALUE_X)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> maxValueXBinding;

    // container ids
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_MAIN_ELEMENT_ID)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> mainElementIdBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_SCROLL_CONTAINER_ID)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> scrollContainerIdBinding;

    // bar
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_BAR_BREAK_WIDTH)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> barBreakWidthBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_BAR_HEIGHT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> barHeightBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_BAR_BREAK_HEIGHT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> barBreakHeightBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_PERCENT_DARK_COLOR_ON_MOVE)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> percentDarkColorOnMoveBinding;

    // base margin
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LEFT_MARGIN_CHART)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> leftMarginChartBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_RIGHT_MARGIN_CHART)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> rightMarginChartBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TOP_MARGIN_CHART)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> topMarginChartBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_BOTTOM_MARGIN_CHART)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> bottomMarginChartBinding;

    // scroll container
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_SCROLL_SIZE_CHART)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> scrollSizeChartBinding;

    // lines, value lines
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_PROTRUDING_LINES_HEIGHT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> protrudingLinesHeightBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LABEL_TEXT_HEIGHT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> labelTextHeightBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LABEL_TEXT_FONT_SIZE)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> labelTextFontSizeBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LINE_STROKE_WIDTH)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> lineStrokeWidthBinding;

    // legend
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TOP_MARGIN_LEGEND)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> topMarginLegendBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LEGEND_COLOR_WIDTH)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> legendColorWidthBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LEGEND_COLOR_HEIGHT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> legendColorHeightBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LEGEND_FONT_SIZE)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> legendFontSizeBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LEGEND_BREAK_COLOR_TEXT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> legendBreakColorTextBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LEGEND_ELEMENT_BREAK_WIDTH)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> legendElementBreakWidthBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LEGEND_ELEMENT_BREAK_HEIGHT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> legendElementBreakHeightBinding;

    // tooltip
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TOOLTIP_OPACITY)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> tooltipOpacityBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TOOLTIP_POINTER_WIDTH)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> tooltipPointerWidthBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TOOLTIP_POINTER_HEIGHT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> tooltipPointerHeightBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TOOLTIP_CORNER_RADIUS)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> tooltipCornerRadiusBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TOOLTIP_FONT_SIZE)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> tooltipFontSizeBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TOOLTIP_PADDING)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> tooltipPaddingBinding;

    // label name prop
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LABEL_NAME_FONT_SIZE)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> labelNameFontSizeBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LABEL_NAME_BREAK)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> labelNameBreakBinding;

    public ChartTime24FhDP(Form form){
        super(form);
    }

    @Override
    public void init(){
        super.init();

        // base parameters
        if(vAxisNameBinding != null){
            BindingResult bindingResultvAxisName = vAxisNameBinding.getBindingResult();
            if(bindingResultvAxisName != null){
                if(bindingResultvAxisName.getValue() != null){
                    this.vAxisName = convertValue(bindingResultvAxisName.getValue(), String.class);
                }
            }
        }
        if(hAxisNameBinding != null){
            BindingResult bindingResulthAxisName = hAxisNameBinding.getBindingResult();
            if(bindingResulthAxisName != null){
                if(bindingResulthAxisName.getValue() != null){
                    this.hAxisName = convertValue(bindingResulthAxisName.getValue(), String.class);
                }
            }
        }
        if(bgColorBinding != null){
            BindingResult bindingResultBgColor = bgColorBinding.getBindingResult();
            if(bindingResultBgColor != null){
                if(bindingResultBgColor.getValue() != null){
                    this.bgColor = convertValue(bindingResultBgColor.getValue(), String.class);
                }
            }
        }
        if(tooltipBgColorBinding != null){
            BindingResult bindingResultTooltipBgColor = tooltipBgColorBinding.getBindingResult();
            if(bindingResultTooltipBgColor != null){
                if(bindingResultTooltipBgColor.getValue() != null){
                    this.tooltipBgColor = convertValue(bindingResultTooltipBgColor.getValue(), String.class);
                }
            }
        }
        if(axisLabelColorBinding != null){
            BindingResult bindingResultAxisLabelColor= axisLabelColorBinding.getBindingResult();
            if(bindingResultAxisLabelColor != null){
                if(bindingResultAxisLabelColor.getValue() != null){
                    this.axisLabelColor = convertValue(bindingResultAxisLabelColor.getValue(), String.class);
                }
            }
        }
        if(lineColorBinding != null){
            BindingResult bindingResultLineColor = lineColorBinding.getBindingResult();
            if(bindingResultLineColor != null){
                if(bindingResultLineColor.getValue() != null){
                    this.lineColor = convertValue(bindingResultLineColor.getValue(), String.class);
                }
            }
        }
        if(tooltipColorBinding != null){
            BindingResult bindingResultTooltipColor = tooltipColorBinding.getBindingResult();
            if(bindingResultTooltipColor != null){
                if(bindingResultTooltipColor.getValue() != null){
                    this.tooltipColor = convertValue(bindingResultTooltipColor.getValue(), String.class);
                }
            }
        }
        if(fontFamilyBinding != null){
            BindingResult bindingResultFontFamily = fontFamilyBinding.getBindingResult();
            if(bindingResultFontFamily != null){
                if(bindingResultFontFamily.getValue() != null){
                    this.fontFamily = convertValue(bindingResultFontFamily.getValue(), String.class);
                }
            }
        }
        if(barsBinding != null){
            BindingResult bindingResultBars = barsBinding.getBindingResult();
            if(bindingResultBars != null){
                if(bindingResultBars.getValue() != null){
                    this.bars = convertValue(bindingResultBars.getValue(), List.class);
                }
            }
        }
        if(highlightedValueBinding != null){
            BindingResult bindingResult = highlightedValueBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.highlightedValue = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(highlightedStrokeBinding != null){
            BindingResult bindingResult = highlightedStrokeBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.highlightedStroke = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(gradationBinding != null){
            BindingResult bindingResult = gradationBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.gradation = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(maxValueXBinding != null){
            BindingResult bindingResult = maxValueXBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.maxValueX = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

        // container ids
        if(mainElementIdBinding != null){
            BindingResult bindingResult = mainElementIdBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.mainElementId = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(scrollContainerIdBinding != null){
            BindingResult bindingResult = scrollContainerIdBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.scrollContainerId = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

        // bar
        if(barBreakWidthBinding != null){
            BindingResult bindingResult = barBreakWidthBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.barBreakWidth = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(barHeightBinding != null){
            BindingResult bindingResult = barHeightBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.barHeight = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(barBreakHeightBinding != null){
            BindingResult bindingResult = barBreakHeightBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.barBreakHeight = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(percentDarkColorOnMoveBinding != null){
            BindingResult bindingResult = percentDarkColorOnMoveBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.percentDarkColorOnMove = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

        // base margin
        if(leftMarginChartBinding != null){
            BindingResult bindingResult = leftMarginChartBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.leftMarginChart = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(rightMarginChartBinding != null){
            BindingResult bindingResult = rightMarginChartBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.rightMarginChart = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(topMarginChartBinding != null){
            BindingResult bindingResult = topMarginChartBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.topMarginChart = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(bottomMarginChartBinding != null){
            BindingResult bindingResult = bottomMarginChartBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.bottomMarginChart = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

        // scroll container
        if(scrollSizeChartBinding != null){
            BindingResult bindingResult = scrollSizeChartBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.scrollSizeChart = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

        // lines, value lines
        if(protrudingLinesHeightBinding != null){
            BindingResult bindingResult = protrudingLinesHeightBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.protrudingLinesHeight = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(labelTextHeightBinding != null){
            BindingResult bindingResult = labelTextHeightBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.labelTextHeight = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(labelTextFontSizeBinding != null){
            BindingResult bindingResult = labelTextFontSizeBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.labelTextFontSize = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(lineStrokeWidthBinding != null){
            BindingResult bindingResult = lineStrokeWidthBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.lineStrokeWidth = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

        // legend
        if(topMarginLegendBinding != null){
            BindingResult bindingResult = topMarginLegendBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.topMarginLegend = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(legendColorWidthBinding != null){
            BindingResult bindingResult = legendColorWidthBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.legendColorWidth = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(legendColorHeightBinding != null){
            BindingResult bindingResult = legendColorHeightBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.legendColorHeight = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(legendFontSizeBinding != null){
            BindingResult bindingResult = legendFontSizeBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.legendFontSize = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(legendBreakColorTextBinding != null){
            BindingResult bindingResult = legendBreakColorTextBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.legendBreakColorText = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(legendElementBreakWidthBinding != null){
            BindingResult bindingResult = legendElementBreakWidthBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.legendElementBreakWidth = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(legendElementBreakHeightBinding != null){
            BindingResult bindingResult = legendElementBreakHeightBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.legendElementBreakHeight = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

        // tooltip
        if(tooltipOpacityBinding != null){
            BindingResult bindingResult = tooltipOpacityBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.tooltipOpacity = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(tooltipPointerWidthBinding != null){
            BindingResult bindingResult = tooltipPointerWidthBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.tooltipPointerWidth = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(tooltipPointerHeightBinding != null){
            BindingResult bindingResult = tooltipPointerHeightBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.tooltipPointerHeight = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(tooltipCornerRadiusBinding != null){
            BindingResult bindingResult = tooltipCornerRadiusBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.tooltipCornerRadius = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(tooltipFontSizeBinding != null){
            BindingResult bindingResult = tooltipFontSizeBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.tooltipFontSize = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(tooltipPaddingBinding != null){
            BindingResult bindingResult = tooltipPaddingBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.tooltipPadding = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }

        // label name prop
        if(labelNameFontSizeBinding != null){
            BindingResult bindingResult = labelNameFontSizeBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.labelNameFontSize = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
        if(labelNameBreakBinding != null){
            BindingResult bindingResult = labelNameBreakBinding.getBindingResult();
            if(bindingResult != null){
                if(bindingResult.getValue() != null){
                    this.labelNameBreak = convertValue(bindingResult.getValue(), String.class);
                }
            }
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        // base parameters
        if (vAxisNameBinding != null) {
            vAxisName = vAxisNameBinding.resolveValueAndAddChanges(
                    this, elementChange, vAxisName, ATTR_V_AXIS_NAME);
        }
        if (hAxisNameBinding != null) {
            hAxisName = hAxisNameBinding.resolveValueAndAddChanges(
                    this, elementChange, hAxisName, ATTR_H_AXIS_NAME);
        }
        if (bgColorBinding != null) {
            bgColor = bgColorBinding.resolveValueAndAddChanges(
                    this, elementChange, bgColor, ATTR_BG_COLOR);
        }
        if (tooltipBgColorBinding != null) {
            tooltipBgColor = tooltipBgColorBinding.resolveValueAndAddChanges(
                    this, elementChange, tooltipBgColor, ATTR_TOOLTIP_BG_COLOR);
        }
        if (axisLabelColorBinding != null) {
            axisLabelColor = axisLabelColorBinding.resolveValueAndAddChanges(
                    this, elementChange, axisLabelColor, ATTR_AXIS_LABEL_COLOR);
        }
        if (lineColorBinding != null) {
            lineColor = lineColorBinding.resolveValueAndAddChanges(
                    this, elementChange, lineColor, ATTR_LINE_COLOR);
        }
        if (tooltipColorBinding != null) {
            tooltipColor = tooltipColorBinding.resolveValueAndAddChanges(
                    this, elementChange, tooltipColor, ATTR_TOOLTIP_COLOR);
        }
        if (fontFamilyBinding != null) {
            fontFamily = fontFamilyBinding.resolveValueAndAddChanges(
                    this, elementChange, fontFamily, ATTR_FONT_FAMILY);
        }
        if (barsBinding != null) {
            bars = barsBinding.resolveValueAndAddChanges(this, elementChange, bars, ATTR_BARS);
        }
        if (highlightedValueBinding != null) {
            highlightedValue = highlightedValueBinding.resolveValueAndAddChanges(
                    this, elementChange, highlightedValue, ATTR_HIGHLIGHTED_VALUE);
        }
        if (highlightedStrokeBinding != null) {
            highlightedStroke = highlightedStrokeBinding.resolveValueAndAddChanges(
                    this, elementChange, highlightedStroke, ATTR_HIGHLIGHTED_STROKE);
        }
        if (gradationBinding != null) {
            gradation = gradationBinding.resolveValueAndAddChanges(
                    this, elementChange, gradation, ATTR_GRADATION);
        }
        if (maxValueXBinding != null) {
            maxValueX = maxValueXBinding.resolveValueAndAddChanges(
                    this, elementChange, maxValueX, ATTR_MAX_VALUE_X);
        }

        // container ids
        if (mainElementIdBinding != null) {
            mainElementId = mainElementIdBinding.resolveValueAndAddChanges(
                    this, elementChange, mainElementId, ATTR_MAIN_ELEMENT_ID);
        }
        if (scrollContainerIdBinding != null) {
            scrollContainerId = scrollContainerIdBinding.resolveValueAndAddChanges(
                    this, elementChange, scrollContainerId, ATTR_SCROLL_CONTAINER_ID);
        }

        // bar
        if (barBreakWidthBinding != null) {
            barBreakWidth = barBreakWidthBinding.resolveValueAndAddChanges(
                    this, elementChange, barBreakWidth, ATTR_BAR_BREAK_WIDTH);
        }
        if (barHeightBinding != null) {
            barHeight = barHeightBinding.resolveValueAndAddChanges(
                    this, elementChange, barHeight, ATTR_BAR_HEIGHT);
        }
        if (barBreakHeightBinding != null) {
            barBreakHeight = barBreakHeightBinding.resolveValueAndAddChanges(
                    this, elementChange, barBreakHeight, ATTR_BAR_BREAK_HEIGHT);
        }
        if (percentDarkColorOnMoveBinding != null) {
            percentDarkColorOnMove = percentDarkColorOnMoveBinding.resolveValueAndAddChanges(
                    this, elementChange, percentDarkColorOnMove, ATTR_PERCENT_DARK_COLOR_ON_MOVE);
        }

        // base margin
        if (leftMarginChartBinding != null) {
            leftMarginChart = leftMarginChartBinding.resolveValueAndAddChanges(
                    this, elementChange, leftMarginChart, ATTR_LEFT_MARGIN_CHART);
        }
        if (rightMarginChartBinding != null) {
            rightMarginChart = rightMarginChartBinding.resolveValueAndAddChanges(
                    this, elementChange, rightMarginChart, ATTR_RIGHT_MARGIN_CHART);
        }
        if (topMarginChartBinding != null) {
            topMarginChart = topMarginChartBinding.resolveValueAndAddChanges(
                    this, elementChange, topMarginChart, ATTR_TOP_MARGIN_CHART);
        }
        if (bottomMarginChartBinding != null) {
            bottomMarginChart = bottomMarginChartBinding.resolveValueAndAddChanges(
                    this, elementChange, bottomMarginChart, ATTR_BOTTOM_MARGIN_CHART);
        }

        // scroll container
        if (scrollSizeChartBinding != null) {
            scrollSizeChart = scrollSizeChartBinding.resolveValueAndAddChanges(
                    this, elementChange, scrollSizeChart, ATTR_SCROLL_SIZE_CHART);
        }

        // lines, value lines
        if (protrudingLinesHeightBinding != null) {
            protrudingLinesHeight = protrudingLinesHeightBinding.resolveValueAndAddChanges(
                    this, elementChange, protrudingLinesHeight, ATTR_PROTRUDING_LINES_HEIGHT);
        }
        if (labelTextHeightBinding != null) {
            labelTextHeight = labelTextHeightBinding.resolveValueAndAddChanges(
                    this, elementChange, labelTextHeight, ATTR_LABEL_TEXT_HEIGHT);
        }
        if (labelTextFontSizeBinding != null) {
            labelTextFontSize = labelTextFontSizeBinding.resolveValueAndAddChanges(
                    this, elementChange, labelTextFontSize, ATTR_LABEL_TEXT_FONT_SIZE);
        }
        if (lineStrokeWidthBinding != null) {
            lineStrokeWidth = lineStrokeWidthBinding.resolveValueAndAddChanges(
                    this, elementChange, lineStrokeWidth, ATTR_LINE_STROKE_WIDTH);
        }

        // legend
        if (topMarginLegendBinding != null) {
            topMarginLegend = topMarginLegendBinding.resolveValueAndAddChanges(
                    this, elementChange, topMarginLegend, ATTR_TOP_MARGIN_LEGEND);
        }
        if (legendColorWidthBinding != null) {
            legendColorWidth = legendColorWidthBinding.resolveValueAndAddChanges(
                    this, elementChange, legendColorWidth, ATTR_LEGEND_COLOR_WIDTH);
        }
        if (legendColorHeightBinding != null) {
            legendColorHeight = legendColorHeightBinding.resolveValueAndAddChanges(
                    this, elementChange, legendColorHeight, ATTR_LEGEND_COLOR_HEIGHT);
        }
        if (legendFontSizeBinding != null) {
            legendFontSize = legendFontSizeBinding.resolveValueAndAddChanges(
                    this, elementChange, legendFontSize, ATTR_LEGEND_FONT_SIZE);
        }
        if (legendBreakColorTextBinding != null) {
            legendBreakColorText = legendBreakColorTextBinding.resolveValueAndAddChanges(
                    this, elementChange, legendBreakColorText, ATTR_LEGEND_BREAK_COLOR_TEXT);
        }
        if (legendElementBreakWidthBinding != null) {
            legendElementBreakWidth = legendElementBreakWidthBinding.resolveValueAndAddChanges(
                    this, elementChange, legendElementBreakWidth, ATTR_LEGEND_ELEMENT_BREAK_WIDTH);
        }
        if (legendElementBreakHeightBinding != null) {
            legendElementBreakHeight = legendElementBreakHeightBinding.resolveValueAndAddChanges(
                    this, elementChange, legendElementBreakHeight, ATTR_LEGEND_ELEMENT_BREAK_HEIGHT);
        }

        // tooltip
        if (tooltipOpacityBinding != null) {
            tooltipOpacity = tooltipOpacityBinding.resolveValueAndAddChanges(
                    this, elementChange, tooltipOpacity, ATTR_TOOLTIP_OPACITY);
        }
        if (tooltipPointerWidthBinding != null) {
            tooltipPointerWidth = tooltipPointerWidthBinding.resolveValueAndAddChanges(
                    this, elementChange, tooltipPointerWidth, ATTR_TOOLTIP_POINTER_WIDTH);
        }
        if (tooltipPointerHeightBinding != null) {
            tooltipPointerHeight = tooltipPointerHeightBinding.resolveValueAndAddChanges(
                    this, elementChange, tooltipPointerHeight, ATTR_TOOLTIP_POINTER_HEIGHT);
        }
        if (tooltipCornerRadiusBinding != null) {
            tooltipCornerRadius = tooltipCornerRadiusBinding.resolveValueAndAddChanges(
                    this, elementChange, tooltipCornerRadius, ATTR_TOOLTIP_CORNER_RADIUS);
        }
        if (tooltipFontSizeBinding != null) {
            tooltipFontSize = tooltipFontSizeBinding.resolveValueAndAddChanges(
                    this, elementChange, tooltipFontSize, ATTR_TOOLTIP_FONT_SIZE);
        }
        if (tooltipPaddingBinding != null) {
            tooltipPadding = tooltipPaddingBinding.resolveValueAndAddChanges(
                    this, elementChange, tooltipPadding, ATTR_TOOLTIP_PADDING);
        }

        // label name prop
        if (labelNameFontSizeBinding != null) {
            labelNameFontSize = labelNameFontSizeBinding.resolveValueAndAddChanges(
                    this, elementChange, labelNameFontSize, ATTR_LABEL_NAME_FONT_SIZE);
        }
        if (labelNameBreakBinding != null) {
            labelNameBreak = labelNameBreakBinding.resolveValueAndAddChanges(
                    this, elementChange, labelNameBreak, ATTR_LABEL_NAME_BREAK);
        }

        return elementChange;
    }
}
