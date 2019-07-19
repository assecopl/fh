package pl.fhframework.model.forms.formatters;

import org.springframework.format.Formatter;
import pl.fhframework.format.FhFormatter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Currency format component.
 *
 * @author Tomasz.Kozlowski (created on 2017-11-07)
 */
@FhFormatter
public class CurrencyFormatter implements Formatter<Double> {
    private static ThreadLocal<DecimalFormat> FORMATTER = ThreadLocal.withInitial(() -> new DecimalFormat("###,###,##0.00"));

    @Override
    public Double parse(String text, Locale locale) throws ParseException {
        return FORMATTER.get().parse(text).doubleValue();
    }

    @Override
    public String print(Double object, Locale locale) {
        return FORMATTER.get().format(object);
    }

}

