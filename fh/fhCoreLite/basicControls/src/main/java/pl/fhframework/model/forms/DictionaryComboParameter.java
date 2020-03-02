package pl.fhframework.model.forms;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.convert.ConversionFailedException;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;


import java.text.ParseException;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;


@DesignerControl(defaultWidth = -1)
@Control(parents = {DictionaryCombo.class}, canBeDesigned = false)
@DocumentedComponent(value = "It is used to construct columns of Table components.", icon = "fa fa-columns")
@ModelElement(type = ModelElementType.HIDDEN)
public class DictionaryComboParameter extends Component implements Boundable{


    public static final String ATTR_VALUE = "value";

    @Getter
    @Setter
    @XMLProperty
    private String name;

    @Getter
    @Setter
    private String rawValue = "";

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_VALUE, aliases = Combo.SELECTED_ITEM_ATTR)
    // when changing @DesignerXMLProperty also change in @OverridenPropertyAnnotations on InputNumber,CheckBox,InputDate,InputText,InputTimestamp
    @DesignerXMLProperty(commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Binding represents value from model of Form, used inside of '{}', like {model}.")
    private ModelBinding modelBinding;

    @JsonIgnore
    @Getter
    protected boolean validConversion = true;

    public DictionaryComboParameter(Form form) {
        super(form);
    }


    protected void processCoversionException(FhBindingException cfe) {
        throw cfe;
    }

}
