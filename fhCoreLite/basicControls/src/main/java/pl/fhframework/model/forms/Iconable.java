package pl.fhframework.model.forms;

import pl.fhframework.Binding;
import pl.fhframework.BindingResult;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.attribute.IconAlignment;

/**
 * Interface which allows form component to have icon inside of it. Implemented class have to have
 * two attributes
 * <pre>
 * <code>&#64;Getter
 * &#64;Setter
 * &#64;XMLProperty
 * &#64;DocumentedComponentAttribute("Icon id. Please refer to http://fontawesome.io/icons/ for all
 * available icons.")
 * private String icon;
 *
 * &#64;Getter
 * &#64;Setter
 * &#64;XMLProperty
 * &#64;DocumentedComponentAttribute("Icon alignment - possible values are before or after. Final
 * alignment depends of component where this attribute is used.")
 * private String iconAlignment;</code></pre>
 *
 * Example of use: <pre><code> &lt;OutputLabel value="This is example" icon="fa fa-image"
 * iconAlignment="after"/&gt;</code></pre>
 */
public interface Iconable {
    String ICON = "icon";

    //TODO remove workaround
    static final Binding BINDING = new Binding();

    ModelBinding<String> getIconBinding();

    String getIcon();

    IconAlignment getIconAlignment();

    default String resolveIconBinding(FormElement formElement, ElementChanges elementChanges) {
        String oldValue = getIcon();
        if (getIconBinding() != null) {
            BindingResult<String> bindingResult = getIconBinding().getBindingResult();
            if (bindingResult != null) {
                String newValue = bindingResult.getValue();
                if (!formElement.areValuesTheSame(newValue, oldValue)) {
                    formElement.refreshView();
                    elementChanges.addChange(ICON, newValue);
                    return newValue;
                }
            }
        }
        return oldValue; // just pass-through
    }
}
