package pl.fhframework.dp.commons.validation;

import java.util.Locale;

public class ParameterMessageInterpolatorFhdp extends org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator {
    private final Locale locale;

    public ParameterMessageInterpolatorFhdp(final Locale locale) {
        this.locale = locale;
    }

    @Override
    public String interpolate(final String messageTemplate, final Context context) {
        if (this.locale == null)
//            return super.interpolate(messageTemplate, context);
            return "$." + messageTemplate.replace("{", "").replace("}", "");
        else
            return this.interpolate(messageTemplate, context, this.locale);
    }
}