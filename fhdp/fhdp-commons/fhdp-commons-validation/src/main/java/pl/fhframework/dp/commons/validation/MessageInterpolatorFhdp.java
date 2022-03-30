package pl.fhframework.dp.commons.validation;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.util.Locale;

public class MessageInterpolatorFhdp extends ResourceBundleMessageInterpolator {
    private final Locale locale;

    public MessageInterpolatorFhdp(final Locale locale, ResourceBundleLocator userResourceBundleLocator) {
        super(userResourceBundleLocator);
        this.locale = locale;
    }

    @Override
    public String interpolate(final String messageTemplate, final Context context) {
        if (this.locale == null)
            return super.interpolate(messageTemplate, context);
//            return "$." + messageTemplate.replace("{", "").replace("}", "");
        else
            return this.interpolate(messageTemplate, context, this.locale);
    }
}