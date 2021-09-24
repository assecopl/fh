package pl.fhframework.model.forms.designer;

import pl.fhframework.core.designer.IDesignerAttributeSupport;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.AvailabilityConfiguration;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.IComponentsReferrer;
import pl.fhframework.validation.FieldValidationResult;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Component's attribute with regex value designer supporting class
 */
public class RegExAttributeDesignerSupport implements IDesignerAttributeSupport<String> {

    @Override
    public Optional<FieldValidationResult> validate(Form<?> form, Component component, String oldValue, String newValue, Type type) {
        try {
            if (newValue != null) {
                Pattern.compile(newValue);
            }
        } catch (PatternSyntaxException e) {
            return Optional.of(new FieldValidationResult(PresentationStyleEnum.BLOCKER, "Invalid regular expression: " + newValue));
        }
        return Optional.empty();
    }
}
