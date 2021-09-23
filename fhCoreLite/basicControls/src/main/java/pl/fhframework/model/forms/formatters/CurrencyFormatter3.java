package pl.fhframework.model.forms.formatters;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;
import pl.fhframework.format.FhFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

@FhFormatter("currencyFormatter3")
public class CurrencyFormatter3 implements Formatter<BigDecimal> {
    private static final ThreadLocal<DecimalFormat> customFormat = ThreadLocal.withInitial(() -> new DecimalFormat() {{
        DecimalFormatSymbols pSymbols = getDecimalFormatSymbols();
        pSymbols.setDecimalSeparator(',');
        pSymbols.setGroupingSeparator(' ');
        setDecimalFormatSymbols(pSymbols);
        setGroupingUsed(true);
        setParseBigDecimal(true);
        setMinimumFractionDigits(0);
        setMaximumFractionDigits(0);
        setRoundingMode(RoundingMode.HALF_UP);
    }});

    @Override
    public BigDecimal parse(String stringValue, Locale locale) throws ParseException {
        if (!StringUtils.isEmpty(stringValue)) stringValue = stringValue.replace('.', ',');
        return (BigDecimal) customFormat.get().parse(stringValue);
    }

    @Override
    public String print(BigDecimal value, Locale locale) {
        String pRetVal = "";
        if (value != null) {
            pRetVal = customFormat.get().format(value);
        }
        return pRetVal;
    }

}

