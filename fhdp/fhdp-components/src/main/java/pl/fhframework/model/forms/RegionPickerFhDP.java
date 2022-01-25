package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.SessionManager;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.events.I18nFormElement;
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
public class RegionPickerFhDP extends FormElement implements I18nFormElement, IChangeableByClient, Boundable {

    public static final String ATTR_MAP = "map";
    public static final String ATTR_BG_COLOR = "bgColor";
    public static final String ATTR_CODE = "code";

    @Getter
    private String map;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_MAP) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component map starter")
    private ModelBinding<String> mapModelBinding;

    @Getter
    private String code;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_CODE) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component country code")
    private ModelBinding<String> codeModelBinding;

    @Getter
    private String bgColor;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_BG_COLOR) //Powiązanie bindingu z property
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component background color")
    private ModelBinding<String> bgColorModelBinding;

    @Getter
    private String language;

    public RegionPickerFhDP(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        language = SessionManager.getUserSession().getLanguage().toLanguageTag();
        if (mapModelBinding != null) {
            BindingResult bidingResult = mapModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.map = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }
        if (bgColorModelBinding != null) {
            BindingResult bidingResult = bgColorModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.bgColor = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }

        if (codeModelBinding != null) {
            BindingResult bidingResult = codeModelBinding.getBindingResult();
            if (bidingResult != null) {
                if (bidingResult.getValue() != null) {
                    this.code = convertValue(bidingResult.getValue(), String.class);
                }
            }
        }
    }

    @Override
    public void onSessionLanguageChange(String lang) {
        this.language = lang;
        this.refreshView();
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (mapModelBinding != null) {
            map = mapModelBinding.resolveValueAndAddChanges(this, elementChange, map, ATTR_MAP);
        }
        if (bgColorModelBinding != null) {
            bgColor = mapModelBinding.resolveValueAndAddChanges(this, elementChange, bgColor, ATTR_BG_COLOR);
        }

        if (codeModelBinding != null) {
            code = mapModelBinding.resolveValueAndAddChanges(this, elementChange, code, ATTR_CODE);
        }
        return elementChange;
    }


    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        switch (eventData.getEventType()) {
            case "countrySelected":
                String code = eventData.getActionName();
                this.code = code;
                this.updateView();
                return Optional.empty();
            default:
                return super.getEventHandler(eventData);
        }
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        if (codeModelBinding != null) {
            this.code = StringUtils.emptyToNull(valueChange.getStringAttribute("code"));
            this.updateBindingForValue(this.code, codeModelBinding, codeModelBinding.getBindingExpression(), this.getOptionalFormatter());
        }
    }
}
