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

@Getter
@Setter
@DesignerControl(defaultWidth = 1)
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class, PanelHeaderFhDP.class, PanelFhDP.class}, invalidParents = {Table.class, Widget.class, Repeater.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.IMAGE_HTML_MD, value = "Embedded iframe view", icon = "fa fa-eye")
public class PageExistBroadcastFhDP extends FormElement {

    public static final String ATTR_TYPE = "type";
    public static final String ATTR_CHECK_URL = "checkUrl";
    public static final String ATTR_SUCCESS_URL = "successUrl";
    public static final String ATTR_FAIL_URL = "failUrl";
    public static final String ATTR_TIMEOUT = "timeout";
    public static final String ATTR_TRIGGER = "trigger";
    public static final String ATTR_PARENT_ORIGIN = "parentOrigin";

    @Getter
    private String type;

    @Getter
    private String checkUrl;

    @Getter
    private String successUrl;

    @Getter
    private String failUrl;

    @Getter
    private String timeout;

    @Getter
    private Boolean trigger;

    @Getter
    private String parentOrigin;


    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TYPE)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> typeBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_CHECK_URL)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> checkUrlBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_SUCCESS_URL)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> successUrlBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_FAIL_URL)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> failUrlBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TIMEOUT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> timeoutBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TRIGGER)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<Boolean> triggerBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TRIGGER)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> parentOriginBinding;

    public PageExistBroadcastFhDP(Form form){
        super(form);
    }

    @Override
    public void init(){
        super.init();
        if(typeBinding != null){
            BindingResult bindingResultTitle = typeBinding.getBindingResult();
            if(bindingResultTitle != null){
                if(bindingResultTitle.getValue() != null){
                    this.type = convertValue(bindingResultTitle.getValue(), String.class);
                }
            }
        }

        if (this.type.equals("parent")) {
            if(checkUrlBinding != null){
                BindingResult bindingResultTitle = checkUrlBinding.getBindingResult();
                if(bindingResultTitle != null){
                    if(bindingResultTitle.getValue() != null){
                        this.checkUrl = convertValue(bindingResultTitle.getValue(), String.class);
                    }
                }
            }
            if(successUrlBinding != null){
                BindingResult bindingResultTitle = successUrlBinding.getBindingResult();
                if(bindingResultTitle != null){
                    if(bindingResultTitle.getValue() != null){
                        this.successUrl = convertValue(bindingResultTitle.getValue(), String.class);
                    }
                }
            }
            if(failUrlBinding != null){
                BindingResult bindingResultTitle = failUrlBinding.getBindingResult();
                if(bindingResultTitle != null){
                    if(bindingResultTitle.getValue() != null){
                        this.failUrl = convertValue(bindingResultTitle.getValue(), String.class);
                    }
                }
            }
            if(timeoutBinding != null){
                BindingResult bindingResultTitle = timeoutBinding.getBindingResult();
                if(bindingResultTitle != null){
                    if(bindingResultTitle.getValue() != null){
                        this.timeout = convertValue(bindingResultTitle.getValue(), String.class);
                    }
                }
            }
            if(triggerBinding != null){
                BindingResult bindingResultTitle = triggerBinding.getBindingResult();
                if(bindingResultTitle != null){
                    if(bindingResultTitle.getValue() != null){
                        this.trigger = convertValue(bindingResultTitle.getValue(), Boolean.class);
                    }
                }
            }
        } else if (this.type.equals("child")) {
            if(parentOriginBinding != null){
                BindingResult bindingResultTitle = parentOriginBinding.getBindingResult();
                if(bindingResultTitle != null){
                    if(bindingResultTitle.getValue() != null){
                        this.parentOrigin = convertValue(bindingResultTitle.getValue(), String.class);
                    }
                }
            }
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (triggerBinding != null) {
            trigger = triggerBinding.resolveValueAndAddChanges(this, elementChange, trigger, ATTR_TRIGGER);
        }

        return elementChange;
    }
}

