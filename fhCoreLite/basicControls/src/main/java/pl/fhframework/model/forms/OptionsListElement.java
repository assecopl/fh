package pl.fhframework.model.forms;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;

@Control(parents = {OptionsList.class}, canBeDesigned = true)
public class OptionsListElement extends FormElement {
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_CHECKED = "checked";
    private static final String ATTR_TITLE = "title";
    private static final String ATTR_ICON = "icon";

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_ICON)
    private ModelBinding iconBinding;

    @Getter
    @Setter
    private String icon;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_VALUE)
    @DesignerXMLProperty(previewValueProvider = BindingExpressionDesignerPreviewProvider.class)
    private Object valueBinding;

    @Getter
    @Setter
    private Object value;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_CHECKED)
    private Boolean checkedBinding;

    @Getter
    @Setter
    private Boolean checked;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_TITLE)
    private Boolean titleBinding;

    @Getter
    @Setter
    private Boolean title;

    public OptionsListElement(Form form) {
        super(form);
    }
}
