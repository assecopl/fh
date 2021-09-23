package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.forms.attribute.IconAlignment;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

/**
 * Created by k.czajkowski on 19.01.2017.
 */
@TemplateControl(tagName = "fh-dropdown-item")
@Control(parents = {Dropdown.class, Repeater.class}, canBeDesigned = true)
public class DropdownItem extends FormElement implements Iconable {

    private static final String ATTR_ON_CLICK = "onClick";
    public static final String ATTR_URL = "url";
    public static final String ATTR_VALUE = "value";
    public static final String ATTR_ICON = "icon";


    @Getter
    private String value = "Set value";

    @Getter
    private String url;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    @DocumentedComponentAttribute(value = "If the DropdownItem is clicked that method will be executed. Action is fired, while component is active.")
    private ActionBinding onClick;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_ICON)
    @DocumentedComponentAttribute(boundable = true, value = "Icon id. Please refer to http://fontawesome.io/icons/ for all available icons.")
    private ModelBinding iconBinding;

    @Getter
    private String icon;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "BEFORE")
    @DocumentedComponentAttribute(defaultValue = "BEFORE", boundable = true, value = "Icon alignment - possible values are before or after. Final alignment depends of component where this attribute is used.")
    private IconAlignment iconAlignment;

    @Getter
    @Setter
    @JsonIgnore
    @XMLProperty(required = true, value = ATTR_VALUE)
    @DocumentedComponentAttribute(boundable = true, value = "Represents text value for created component.")
    private ModelBinding modelBindingForValue;

    @Getter
    @Setter
    @JsonIgnore
    @XMLProperty(value = ATTR_URL)
    @DocumentedComponentAttribute(boundable = true, value = "Hyperlink to resource")
    private ModelBinding modelBindingForUrl;

    public DropdownItem(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        BindingResult bindingResult;
        if (modelBindingForUrl != null) {
            bindingResult = modelBindingForUrl.getBindingResult();
            if (bindingResult != null) {
                this.url = convertBindingValueToString(bindingResult);
            }
        }

        if (modelBindingForValue != null) {
            bindingResult = modelBindingForValue.getBindingResult();
            if (bindingResult != null) {
                this.value = convertBindingValueToString(bindingResult);
            }
        }
    }

    private void processValue(ElementChanges elementChanges) {
        if (modelBindingForValue != null) {
            BindingResult bindingResult = modelBindingForValue.getBindingResult();
            if (bindingResult != null) {
                String newLabelValue = this.convertValueToString(bindingResult.getValue(), null);
                if(iconBinding != null){
                    icon = resolveIconBinding(this, elementChanges);
                }
                if (!areValuesTheSame(newLabelValue, value)) {
                    this.refreshView();
                    this.value = newLabelValue;
                    elementChanges.addChange(ATTR_VALUE, this.value);
                }
            }
        }
    }

    private void processURL(ElementChanges elementChanges) {
        if (modelBindingForUrl != null) {
            BindingResult bindingResultForUrl = modelBindingForUrl.getBindingResult();
            if (bindingResultForUrl != null) {
                String newUrl = this.convertValueToString(bindingResultForUrl.getValue(), null);
                if (!areValuesTheSame(newUrl, url)) {
                    this.refreshView();
                    this.url = newUrl;
                    elementChanges.addChange(ATTR_URL, this.url);
                }
            }
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        processValue(elementChanges);
        processURL(elementChanges);
        return elementChanges;
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (eventData.getEventType().equals(ATTR_ON_CLICK)) {
            return Optional.ofNullable(onClick);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public ModelBinding getIconBinding() {
        return iconBinding;
    }

    public void setOnClick(ActionBinding onClick) {
        this.onClick = onClick;
    }

    public IActionCallbackContext setOnClick(IActionCallback onClick) {
        return CallbackActionBinding.createAndSet(onClick, this::setOnClick);
    }
}
