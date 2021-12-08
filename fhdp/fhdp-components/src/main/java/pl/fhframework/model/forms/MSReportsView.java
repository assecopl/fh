package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.widgets.Widget;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

@Getter
@Setter
@DesignerControl(defaultWidth = 12)
@Control(parents = {PanelGroup.class, Tab.class, Row.class, Form.class, Group.class}, invalidParents = {Table.class, Widget.class, Repeater.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.IMAGE_HTML_MD, value = "Embedded iframe view", icon = "fa fa-eye")
public class MSReportsView extends FormElement implements IChangeableByClient, Boundable {

    public static final String ATTR_CONTENT = "content";
    public static final String ATTR_LINK = "link";
    private static final String ATTR_ON_LINK_CLICKED = "onLinkClicked";

    @Getter
    @XMLProperty(value = ATTR_ON_LINK_CLICKED)
    private ActionBinding onLinkClicked;

    @Getter
    private String content;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_CONTENT) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component content starter")
    private ModelBinding<String> contentModelBinding;

    @Getter
    private String link;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LINK) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component link")
    private ModelBinding<String> linkModelBinding;


    public MSReportsView(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        if (contentModelBinding != null) {
            BindingResult bidingResult = contentModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.content = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }
        if (linkModelBinding != null) {
            BindingResult bidingResult = linkModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.link = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (contentModelBinding != null) {
            content = contentModelBinding.resolveValueAndAddChanges(this, elementChange, content, ATTR_CONTENT);
        }
        if (linkModelBinding != null) {
            link = contentModelBinding.resolveValueAndAddChanges(this, elementChange, link, ATTR_LINK);
        }
        return elementChange;
    }


    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (onLinkClicked.getActionName().equals(eventData.getEventType())) {
            String link = eventData.getActionName();
            this.link = link;
            return Optional.ofNullable(onLinkClicked);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        if (contentModelBinding != null) {
            this.content = StringUtils.emptyToNull(valueChange.getStringAttribute("content"));
            this.updateBindingForValue(this.content, contentModelBinding, contentModelBinding.getBindingExpression(), this.getOptionalFormatter());
        }
        if (linkModelBinding != null) {
            this.link = StringUtils.emptyToNull(valueChange.getStringAttribute("link"));
            this.updateBindingForValue(this.link, linkModelBinding, linkModelBinding.getBindingExpression(), this.getOptionalFormatter());
        }
    }

    public void setOnLinkClicked(ActionBinding onLinkClicked) {
        this.onLinkClicked = onLinkClicked;
    }

    public IActionCallbackContext setOnLinkClicked(IActionCallback onLinkClicked) {
        return CallbackActionBinding.createAndSet(onLinkClicked, this::setOnLinkClicked);
    }
}
