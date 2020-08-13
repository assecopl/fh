package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.DesignerControl;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.attribute.*;

import java.util.Set;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.LOOK_AND_STYLE;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.SPECIFIC;

/**
 * Base component/root of all visual and non visual components. Contains common logic inherited
 * by descendant components. Created by Gabriel on 2015-11-20.
 */
@DesignerControl
@ModelElement(type = ModelElementType.HIDDEN)
public abstract class FormElement extends Component {

    private static final String PIXEL_UNIT = "px";
    private static final String PERCENTAGE_UNIT = "%";
    private static final String PERCENTAGE_FULL_WIDTH = "100" + PERCENTAGE_UNIT;
    private static final String PIXED_OR_PERCENTAGE_REGEX = "(\\-)?[0-9]*|[0-9]+px$|[0-9][0-9]%$|[1][0][0]%$";
    private static final String PIXED_REGEX = "(-?)([0-9]+)((px)?)";
    private static final String PIXED_POSITIVE_REGEX = "([0-9]+)((px)?)";
    private static final String HINT_ATTR = "hint";
    private static final String HINT_PLACEMENT_ATTR = "hintPlacement";
    private static final String HINT_TRIGGER_ATTR = "hintTrigger";
    private static final String HINT_TYPE_ATTR = "hintType";


    @Getter
    @Setter
    @XMLProperty(value = "htmlAccessibilityRole")
    @DocumentedComponentAttribute(value = "Defines role of content presented by this element.Used to improve Accessibility. https://www.w3.org/TR/wai-aria/#landmark_roles")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 90)
    protected String htmlAccessibilityRole;

    /**
     * Height of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 99)
    @DocumentedComponentAttribute("Component height in \"px\" or \"%\", \"px\" is default height unit. There should not be any character between number and unit - height=\"80px\" is valid, height=\"80 px\" is invalid.")
    private String height;

    /**
     * Inline style of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 80, skip = true)
    private String inlineStyle;

    /**
     * Inline style of the component's wrapper
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 79, skip = true)
    private String wrapperStyle;

    /**
     * Push following components to the right
     */
    @Getter
    @Setter
    private boolean push;

    /**
     * Size of the component
     */
    @Getter
    @Setter
    @XMLProperty(aliases = {"size"})
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 100)
    @DocumentedComponentAttribute(defaultValue = "\"md-12\" for all components but not Button FileUpload or FileDownload - \"md-2\"", value = "Component bootstrap size option to place component in one row and different column width. For Column this value should be numeric, because it will be translated to percentage value. ")
    private String width;

    /**
     * Pull-left of the component
     */
    @Getter
    @Setter
    @XMLProperty(aliases = {"align"})
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 98)
    @DocumentedComponentAttribute(defaultValue = "left", value = "Component bootstrap option to place component on the left, center or right side of the view.")
    private HorizontalAlign horizontalAlign;

    /**
     * Vertical-align of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 97)
    @DocumentedComponentAttribute(defaultValue = "bottom", value = "Option to align component vertically relative to parent element. Available values: top, middle, bottom.")
    private VerticalAlign verticalAlign;

    /**
     * Style classes defined for component, separated by ',' character
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 89)
    @DocumentedComponentAttribute("Component style classes, should be separated by ',' character")
    private String styleClasses;

    /**
     * Hint for component
     */
    @Getter
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 88)
    private String hint;

    /**
     * Hint for component
     */
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = HINT_ATTR)
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 87)
    @DocumentedComponentAttribute(value = "Hint for component, visible after hovering over specified component part", boundable = true)
    private ModelBinding<String> hintBinding;

    /**
     * Placement of the hint for component
     */
    @Getter
    @Setter
    @XMLProperty(value = HINT_PLACEMENT_ATTR)
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 86)
    @DocumentedComponentAttribute(defaultValue = "TOP", value = "Placement of the hint for component. Available values: top, left, right, bottom. If value is not set then position will be chosen dynamically.")
    private HintPlacement hintPlacement;

    /**
     * Placement of the hint for component
     */
    @Getter
    @Setter
    @XMLProperty(value = HINT_TRIGGER_ATTR)
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 85)
    @DocumentedComponentAttribute(defaultValue = "HOVER_FOCUS", value = "Trigger of the hint for component. Available values: HOVER_FOCUS, HOVER. If value is not set then position will be HOVER_FOCUS.")
    private HintTrigger hintTrigger;


    /**
     * Placement of the hint for component
     */
    @Getter
    @Setter
    @XMLProperty(value = HINT_TYPE_ATTR, defaultValue = "STANDARD")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 86)
    @DocumentedComponentAttribute(defaultValue = "STANDARD", value = "Switches between stnadard presentation and static presentation. Static presentation makes hint appears after clikc on '?' icon. Icon will appear after label or as input group element")
    private HintType hintType = HintType.STANDARD;


    /**
     * Left margin of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 66)
    @DocumentedComponentAttribute(value = "Component margin amount in  \"px\" to leave outter gap on the left side. There should not be any character between number and unit - marginLeft=\"10px\" is valid, marginLeft=\"10 px\" is invalid.")
    private String marginLeft;

    /**
     * Right margin of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 66)
    @DocumentedComponentAttribute(value = "Component margin amount in  \"px\" to leave outter gap on the right side. There should not be any character between number and unit - marginRight=\"10px\" is valid, marginRight=\"10 px\" is invalid.")
    private String marginRight;

    /**
     * Top margin of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 66)
    @DocumentedComponentAttribute(value = "Component margin amount in  \"px\" to leave outter gap at the top. There should not be any character between number and unit - marginTop=\"10px\" is valid, marginTop=\"10 px\" is invalid.")
    private String marginTop;

    /**
     * Bottom margin of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 66)
    @DocumentedComponentAttribute(value = "Component margin amount in  \"px\" to leave outter gap at the bottom. There should not be any character between number and unit - marginBottom=\"10px\" is valid, marginBottom=\"10 px\" is invalid.")
    private String marginBottom;

    /**
     * Left padding of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 65)
    @DocumentedComponentAttribute(value = "Component padding amount in  \"px\" to leave outter gap at the bottom. There should not be any character between number and unit - paddingLeft=\"10px\" is valid, paddingLeft=\"10 px\" is invalid. Only positive values.")
    private String paddingLeft;

    /**
     * Right padding of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 65)
    @DocumentedComponentAttribute(value = "Component padding amount in  \"px\" to leave outter gap at the bottom. There should not be any character between number and unit - paddingRight=\"10px\" is valid, paddingRight=\"10 px\" is invalid. Only positive values.")
    private String paddingRight;

    /**
     * Top padding of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 65)
    @DocumentedComponentAttribute(value = "Component padding amount in  \"px\" to leave outter gap at the bottom. There should not be any character between number and unit - paddingTop=\"10px\" is valid, paddingTop=\"10 px\" is invalid. Only positive values.")
    private String paddingTop;

    /**
     * Bottom padding of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 65)
    @DocumentedComponentAttribute(value = "Component padding amount in  \"px\" to leave outter gap at the bottom. There should not be any character between number and unit - paddingBottom=\"10px\" is valid, paddingBottom=\"10 px\" is invalid. Only positive values.")
    private String paddingBottom;

    public FormElement(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        height = tryParseAttributeInPixelOrPercentageUnit(height, "height");

        marginLeft = tryParseAttributeInPixelUnit(marginLeft, "marginLeft");
        marginRight = tryParseAttributeInPixelUnit(marginRight, "marginRight");
        marginTop = tryParseAttributeInPixelUnit(marginTop, "marginTop");
        marginBottom = tryParseAttributeInPixelUnit(marginBottom, "marginBottom");

        paddingLeft = tryParseAttributeInPixelUnit(paddingLeft, "paddingLeft", true);
        paddingRight = tryParseAttributeInPixelUnit(paddingRight, "paddingRight", true);
        paddingTop = tryParseAttributeInPixelUnit(paddingTop, "paddingTop", true);
        paddingBottom = tryParseAttributeInPixelUnit(paddingBottom, "paddingBottom", true);
    }

    /**
     * This method try to parse value from xmlAttributeReader based on attributeName. Value can be
     * in pixel or percentage unit. By default if developer sets only integer, value wil be in PIXEL
     * UNIT. If developer want to set value to percentage, integer value can not be bigger that
     * 100%.
     */
    protected String tryParseAttributeInPixelOrPercentageUnit(String attributeValue, String attributeName) {
        FhLogger.trace(this.getClass(), logger -> logger.log("Reading an attribute " + attributeName + " from xml file - attributeName=[{}]", attributeValue));

        if (attributeValue == null) {
            FhLogger.trace(this.getClass(), logger -> logger.log("Attribute " + attributeName + " not set. Default height will be used."));
            return null;
        }
        String trimmedValue = attributeValue.trim().toLowerCase();
        if (!trimmedValue.matches(PIXED_OR_PERCENTAGE_REGEX)) {
            FhLogger.error("Attribute " + attributeName + "=[" + trimmedValue + "] contains illegal characters. Value has been ignored.");
            return null;
        }
        if (trimmedValue.endsWith(PIXEL_UNIT)) {
            FhLogger.trace(this.getClass(), logger -> logger.log("Attribute " + attributeName + "=[{}]", trimmedValue));
            return trimmedValue;
        } else if (trimmedValue.endsWith(PERCENTAGE_UNIT)) {
            String rawValue = trimmedValue.replaceAll(PERCENTAGE_UNIT, "");
            if (Integer.parseInt(rawValue) > 100) {
                FhLogger.error("Attribute  " + attributeName + " is greater than 100%. Value has been set to 100%.");
                return PERCENTAGE_FULL_WIDTH;
            }
            return trimmedValue;
        } else {
            FhLogger.trace(this.getClass(), logger -> logger.log("Attribute  " + attributeName + " does not have unit. Using default px unit:  " + attributeName + "=[{}]", trimmedValue + PIXEL_UNIT));
            return trimmedValue + PIXEL_UNIT;
        }
    }

    /**
     * This method try to parse value from xmlAttributeReader based on attributeName. Value can be
     * in pixel unit. By default if developer sets only integer, value wil be in PIXExs
     */
    protected String tryParseAttributeInPixelUnit(String attributeValue, String attributeName, Boolean onlyPositive) {
        FhLogger.trace(this.getClass(), logger -> logger.log("Reading an attribute " + attributeName + " from xml file - attributeName=[{}]", attributeValue));

        if (attributeValue == null) {
            FhLogger.trace(this.getClass(), logger -> logger.log("Attribute " + attributeName + " not set. Default value will be used."));
            return null;
        }
        String trimmedValue = attributeValue.trim().toLowerCase();
        String regex = onlyPositive ? PIXED_POSITIVE_REGEX : PIXED_REGEX;
        if (!trimmedValue.matches(regex)) {
            FhLogger.error("Attribute " + attributeName + "=[" + trimmedValue + "] contains illegal characters. Value has been ignored.");
            return null;
        }
        if (trimmedValue.endsWith(PIXEL_UNIT)) {
            FhLogger.trace(this.getClass(), logger -> logger.log("Attribute " + attributeName + "=[{}]", trimmedValue));
            return trimmedValue;
        } else {
            FhLogger.trace(this.getClass(), logger -> logger.log("Attribute  " + attributeName + " does not have unit. Using default px unit:  " + attributeName + "=[{}]", trimmedValue + PIXEL_UNIT));
            return trimmedValue + PIXEL_UNIT;
        }
    }

    protected String tryParseAttributeInPixelUnit(String attributeValue, String attributeName) {
        return tryParseAttributeInPixelUnit( attributeValue, attributeName, false);
    }

    public void refreshView(Set<ElementChanges> changeSet) {
        ElementChanges elementChanges = this.updateView();

        if (hintBinding != null) {
            hint = hintBinding.resolveValueAndAddChanges(this, elementChanges, hint, HINT_ATTR);
        }

        IGroupingComponent parent = getGroupingParentComponent();
        boolean stopProcessingUpdateView = isStopProcessingUpdateView();
        while (!stopProcessingUpdateView && parent != null) {
            stopProcessingUpdateView = ((Component) parent).isStopProcessingUpdateView();
            parent = ((Component) parent).getGroupingParentComponent();
        }


        if (!stopProcessingUpdateView && elementChanges.containsAnyChanges()) {
            changeSet.add(elementChanges);
        }

    }

    /**
     * Method updates form components based on linked model and availability rules. Method returns
     * object of changes, it allows to decide which form components are changed and require
     * refreshing
     *
     * @return Returns null, if there was no change or object of changes if there was change after
     * operation
     */
    protected ElementChanges updateView() {
        ElementChanges changedElement = new ElementChanges();
        changedElement.setFormId(getForm().getId());
        changedElement.setFormElementId(this.getId());
        //TODO:visibility changes

        refreshAvailability(changedElement);

        return changedElement;
    }

    /**
     * TODO: In future, this method should avoid all combinations causing element refreshing Logic
     * decides, which argument was modified It will simplify whole logic We should work with tables
     * - amount of elements change (for example removing one element) should not cause sending whole
     * table, moreover this information should not be sent twice (in scope of whole table and just
     * as change of single element) - it could be satisfied by developing logic in FormElement,
     * which would check if element is in the table - if so, then it will inform table (not form)
     * about the changes. Because of that, table would not allow information about element change to
     * form in case of changing size of table.
     */
    protected void refreshView() {
        //TODO:if (this.table != null) table.refreshElementView(this); //  beacuse of that, table could handle redundant refreshing requests
        //TODO:else getForm().refreshElementView(this);
        getForm().refreshElementView(this);
    }

    public void refreshElementToForm() {
        getForm().addToElementIdToFormElement(this);
    }

}
