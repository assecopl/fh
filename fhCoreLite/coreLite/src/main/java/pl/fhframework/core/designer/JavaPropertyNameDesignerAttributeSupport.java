/***********************************************************
 * Opis: patrz niżej w komentarzach javadoc.
 ***********************************************************
 * Osoba odpowiedzialna: Kamil Pliszka
 * Data utworzenia: 2017-09-01 09:51
 ***********************************************************/

package pl.fhframework.core.designer;

import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.validation.FieldValidationResult;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Validator for Component's attribute designer supporting class, which is converted to java property
 */
public class JavaPropertyNameDesignerAttributeSupport implements IDesignerAttributeSupport<String> {
    private Pattern VALID_NAME_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");

    @Override
    public Optional<FieldValidationResult> validate(Form<?> form, Component component, String oldValue, String newValue, Type type) {
        if (newValue == null) {
            return Optional.empty();
        }

        if (!VALID_NAME_PATTERN.matcher(newValue).matches()) {
            return Optional.of(new FieldValidationResult(PresentationStyleEnum.BLOCKER, "Property name must start with a letter and may contain letters, digits and underscores"));
        }

        return Optional.empty();
    }

}
