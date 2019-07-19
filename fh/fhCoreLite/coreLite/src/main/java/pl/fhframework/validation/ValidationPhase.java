package pl.fhframework.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.forms.IValidatedComponent;
import pl.fhframework.core.util.ComponentsUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.FormElement;

import javax.validation.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ValidationPhase {

    @Autowired
    private javax.validation.Validator validator;

    /**
     * Core method for validation process. Here FH first run valdiation based on annotations for
     * model, then validation per IValidatedComponent.
     *
     * @param form              - required for looking through the model of given form and
     *                          IValidatedComponent.
     * @param validationResults - in this objects, validation process store validation errors for
     *                          components
     */
    public void validateModel(Form form, IValidationResults validationResults) {
        ((ValidationResults) validationResults).setFormMode();
        try {
            validateModelBasedOnAnnotation(form, validationResults);
            validateModelPerComponent(form);
        }
        finally {
            ((ValidationResults) validationResults).setBusinessMode();
        }
    }

    private void validateModelPerComponent(Form form) {
        List<FormElement> forms = findComponentsBasedOn(form, x -> x instanceof IValidatedComponent);
        forms.stream().forEach(x -> ((IValidatedComponent) x).validate());
    }

    private void validateModelBasedOnAnnotation(Form form, IValidationResults validationResults) {
        if (form.getModel() == null) {
            return;
        }

        Set<javax.validation.ConstraintViolation<Object>> validate = validator.validate(form.getModel());
        for (javax.validation.ConstraintViolation<Object> constrainResult : validate) {

            // extract last path element
            Path.Node lastPathNode = null;
            for (Path.Node node : constrainResult.getPropertyPath()) {
                lastPathNode = node;
            }

            String attribute = lastPathNode != null ? lastPathNode.getName() : null;
            Object parentObj = constrainResult.getLeafBean();

            if (attribute == null || parentObj == null) {
                continue; // skip this validation error
            }

            // find components
            List<FormElement> foundComponentsBasedOnModelAttribute = findComponentsBasedOn(form, comp -> {
                if (!(comp instanceof IValidatedComponent) || ((IValidatedComponent) comp).getModelBinding() == null) {
                    return false;
                }
                BindingResult<?> bindingResult = ((IValidatedComponent) comp).getModelBinding().getBindingResult();
                if (bindingResult == null) {
                    return false;
                }
                return Objects.equals(attribute, bindingResult.getAttributeName()) && Objects.equals(parentObj, bindingResult.getParent());
            });
            foundComponentsBasedOnModelAttribute.stream().forEach(
                comp -> validationResults.addCustomMessageForComponent((IValidatedComponent) comp,
                        parentObj, attribute, constrainResult.getMessage(), PresentationStyleEnum.BLOCKER)
            );
        }
    }

    private List<FormElement> findComponentsBasedOn(Form form, Predicate<FormElement> predicate) {
        List<FormElement> forms = new ArrayList<>();
        ComponentsUtils.findByExpression(form, predicate.and(x -> !forms.contains(x) && AccessibilityEnum.EDIT == x.getAvailability()), forms);
        return forms;
    }

}
