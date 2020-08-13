package pl.fhframework.core.designer;

import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.core.util.StringUtils;
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

/**
 * Component's id attribute designer supporting class
 */
public class IdAttributeDesignerSupport implements IDesignerAttributeSupport<String> {

    private Pattern VALID_ID_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");

    @Override
    public Optional<FieldValidationResult> validate(Form<?> form, Component component, String oldValue, String newValue, Type type) {
        if (newValue == null) {
            return Optional.empty();
        }

        if (!VALID_ID_PATTERN.matcher(newValue).matches()) {
            return Optional.of(new FieldValidationResult(PresentationStyleEnum.BLOCKER, "Id must start with a letter and may contain letters, digits and underscores"));
        }

        AtomicReference<FieldValidationResult> result = new AtomicReference<>();
        form.doActionForEverySubcomponentInlcudingRepeated(otherComponent -> {
            if (component != otherComponent && Objects.equals(otherComponent.getId(), newValue)) {
                result.set(new FieldValidationResult(PresentationStyleEnum.BLOCKER, "Id must be unique"));
            }
        });
        return Optional.ofNullable(result.get());
    }

    @Override
    public boolean skipOnWrite(Form<?> form, Component component, String value) {
        // check if used in availability configuration
        if (form.getAvailabilityConfiguration() != null) {
            for (AvailabilityConfiguration.FormSetting setting : form.getAvailabilityConfiguration().getSettings()) {
                if (setting.containsComponentId(value)) {
                    return false;
                }
            }
        }
        // check if used in messages and other referrers
        AtomicBoolean listedByReferrers = new AtomicBoolean(false);
        form.doActionForEverySubcomponentInlcudingRepeated(comp -> {
            if (comp instanceof IComponentsReferrer && ((IComponentsReferrer) comp).getComponentIds().contains(value)) {
                listedByReferrers.set(true);
            }
        });
        if (listedByReferrers.get()) {
            return false;
        }

        return value.startsWith(component.getGeneratedIdPrefix());
    }
}
