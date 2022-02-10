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
@DocumentedComponent(category = DocumentedComponent.Category.IMAGE_HTML_MD, value = "Embedded html view", icon = "fa fa-eye")
public class HTMLRawViewFhDP extends FormElement {

    public static final String ATTR_CONTENT = "content";

    @Getter
    private String content;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_CONTENT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    private ModelBinding<String> contentBinding;

    public HTMLRawViewFhDP(Form form){
        super(form);
    }

    @Override
    public void init(){
        super.init();
        if(contentBinding != null){
            BindingResult bindingResultTitle = contentBinding.getBindingResult();
            if(bindingResultTitle != null){
                if(bindingResultTitle.getValue() != null){
                    this.content = convertValue(bindingResultTitle.getValue(), String.class);
                }
            }
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (contentBinding != null) {
            content = contentBinding.resolveValueAndAddChanges(this, elementChange, content, ATTR_CONTENT);
        }

        return elementChange;
    }
}
