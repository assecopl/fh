package pl.fhframework.model.forms.designer;

import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.binding.AdHocModelBinding;
import pl.fhframework.binding.DesignerModelBinding;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.LocaleBundle;

import java.util.List;
import java.util.Optional;

/**
 * I18n aware preview binding value provider.
 */
public abstract class I18nAwareDesignerPreviewProvider implements IDesignerPreviewProvider {

    /**
     * Resolves binding if contains i18n expression
     * @param messageService message service
     * @param modelBinding binding expression
     * @return optional translation
     */
    public Optional<String> getI18nBindingValue(MessageService messageService, Form<?> form, DesignerModelBinding<?> modelBinding) {
        if (modelBinding == null || modelBinding.isStaticValue()) {
            return Optional.empty();
        }

        String bindingExpression = modelBinding.getBindingExpression();

        if (modelBinding.isCombined()) {
            StringBuilder output = new StringBuilder();
            for (AdHocModelBinding.CombinedExpression expression : modelBinding.getCombinedExpressions()) {
                if (expression.isBinding()) {
                    Optional<String> translation = getI18nBindingValueImpl(messageService, form, expression.getValue());

                    // any non-i18n expression results in non-i18n binding
                    if (!translation.isPresent()) {
                        return Optional.empty();
                    }

                    output.append(translation.get());
                } else {
                    output.append(expression.getValue().replace("{", "\\{").replace("}", "\\}")); // replace escaped
                }
            }
            return Optional.of(output.toString());
        } else if (StringUtils.hasSurroundingBraces(bindingExpression)) {
            return getI18nBindingValueImpl(messageService, form, StringUtils.removeSurroundingBraces(bindingExpression));
        } else {
            return Optional.empty();
        }
    }

    protected Optional<String> getI18nBindingValueImpl(MessageService messageService, Form<?> form, String rawBindingExpression) {
        if (!rawBindingExpression.startsWith("$")) {
            return Optional.empty();
        }
        rawBindingExpression = rawBindingExpression.substring(1); // skip leading $

        // separate
        String[] parts = rawBindingExpression.split("\\.", 2);
        String bundleName = parts[0];
        String key = parts[1];

        MessageService.MessageBundle messageBundle;
        if (bundleName.isEmpty()) {
            messageBundle = messageService.getAllBundles();
        } else {
            Optional<String> foundBundle = form.getLocaleBundle().stream()
                    .filter(bundle -> bundleName.equals(bundle.getVar()))
                    .map(LocaleBundle::getBasename)
                    .findFirst();
            if (foundBundle.isPresent()) {
                messageBundle = messageService.getBundle(foundBundle.get());
            } else {
                return Optional.of("Not such i18n bundle: " + bundleName);
            }
        }

        return Optional.of(messageBundle.getMessage(key));
    }
}
