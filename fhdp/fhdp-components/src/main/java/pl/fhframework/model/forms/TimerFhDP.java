package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

@TemplateControl(tagName = "timer-fhdp")
@DesignerControl(defaultWidth = 3)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, value = "Its button with added timer and action trigger",
        icon = "fa fa-outdent")
@Control(parents = {PanelGroup.class, Form.class, Group.class, Row.class, Footer.class}, invalidParents = {Table.class, Repeater.class}, canBeDesigned = false)
public class TimerFhDP extends FormElement{

    public static final String ATTR_ON_INTERVAL = "onInterval";
    public static final String ATTR_INTERVAL = "timeout";

    @Getter
    private Integer timeout = 3000;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_INTERVAL)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "interval of trigger")
    private ModelBinding<Integer> timeoutModelBinding;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(commonUse = true, functionalArea = BEHAVIOR, priority = 90)
    @DocumentedComponentAttribute(value = "If the button has intervel time given that method will be executed in intervel trigger. Action is fired, while component is active.")
    private ActionBinding onInterval;



    public TimerFhDP(Form form) {
        super(form);
    }

    @Override
    public void init(){
        super.init();
        if(timeout != null){
            BindingResult bindingResultInterval = timeoutModelBinding.getBindingResult();
            if(bindingResultInterval != null){
                if(bindingResultInterval.getValue() != null){
                    this.timeout = convertValue(bindingResultInterval.getValue(), Integer.class);
                }
            }
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (timeoutModelBinding != null) {
            timeout = timeoutModelBinding.resolveValueAndAddChanges(this, elementChange, timeout, ATTR_INTERVAL);
        }

        return elementChange;
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (eventData.getEventType().equals(ATTR_ON_INTERVAL)) {
            return Optional.ofNullable(onInterval);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    public IActionCallbackContext setOnInterval(IActionCallback callback) {
        return CallbackActionBinding.createAndSet(callback, this::setOnInterval);
    }

    public void setOnInterval(ActionBinding onInterval) {
        this.onInterval = onInterval;
    }
}
