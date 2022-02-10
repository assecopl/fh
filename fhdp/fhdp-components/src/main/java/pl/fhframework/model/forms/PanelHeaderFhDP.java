package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerControl;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.widgets.Widget;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

@Getter
@Setter
@DesignerControl
@Control(parents = {PanelGroup.class, Tab.class, Row.class, Form.class, Group.class}, invalidParents = {Table.class, Widget.class, Repeater.class}, canBeDesigned = true)
public class PanelHeaderFhDP extends FormElement {

    public static final String ATTR_TITLE = "title";
    public static final String ATTR_INFO = "info";
    public static final String ATTR_ON_CLICK = "onClick";

    @Getter
    private String info;

    @Getter
    private String title;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TITLE)//Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> titleModelBinding;



    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_INFO)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> infoModelBinding;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(commonUse = false, functionalArea = BEHAVIOR, priority = 90)
    private ActionBinding onClick;

    public PanelHeaderFhDP(Form form){
        super(form);
    }

    @Override
    public void init(){
        super.init();
        if(titleModelBinding != null){
            BindingResult bindingResultTitle = titleModelBinding.getBindingResult();
            if(bindingResultTitle != null){
                if(bindingResultTitle.getValue() != null){
                    this.title = convertValue(bindingResultTitle.getValue(), String.class);
                }
            }
        }
        if(infoModelBinding != null){
            BindingResult bindingResultInfo = infoModelBinding.getBindingResult();
            if(bindingResultInfo != null){
                if(bindingResultInfo.getValue() != null){
                    this.info = convertValue(bindingResultInfo.getValue(), String.class);
                }
            }
        }
    }


    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (eventData.getEventType().equals(ATTR_ON_CLICK)) {
            return Optional.ofNullable(onClick);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    public void setOnClick(ActionBinding onClick) {
        this.onClick = onClick;
    }
    public IActionCallbackContext setOnClick(IActionCallback onClick) {
        return CallbackActionBinding.createAndSet(onClick, this::setOnClick);
    }
    @Override
    protected List<ActionBinding> getAvailablityAffectingActions() {
        return Arrays.asList(onClick);
    }




    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (titleModelBinding != null) {
            title = titleModelBinding.resolveValueAndAddChanges(this, elementChange, title, ATTR_TITLE);
        }

        if (infoModelBinding != null) {
            info = infoModelBinding.resolveValueAndAddChanges(this, elementChange, info, ATTR_INFO);
        }

        return elementChange;
    }
}
