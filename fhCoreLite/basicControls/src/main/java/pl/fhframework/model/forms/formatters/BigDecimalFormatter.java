package pl.fhframework.model.forms.formatters;

import org.springframework.util.StringUtils;
import pl.fhframework.format.AutoRegisteredFormatter;
import pl.fhframework.format.FhFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

@FhFormatter("bigDecimalFormatter")
public class BigDecimalFormatter extends AutoRegisteredFormatter<BigDecimal> {
    private static ThreadLocal<DecimalFormat> customFormat = ThreadLocal.withInitial(() -> new DecimalFormat() {{
        DecimalFormatSymbols pSymbols = getDecimalFormatSymbols();
        pSymbols.setDecimalSeparator(',');
        pSymbols.setGroupingSeparator(' ');
        setDecimalFormatSymbols(pSymbols);
        setGroupingUsed(false);
        setParseBigDecimal(true);
        setMinimumFractionDigits(0);
        setMaximumFractionDigits(2);
    }});

    @Override
    public BigDecimal parse(String s, Locale locale) throws ParseException {
        if (!StringUtils.isEmpty(s)) s = s.replace('.', ',');
        return (BigDecimal) customFormat.get().parse(s);
    }

    @Override
    public String print(BigDecimal value, Locale locale) {
        String pRetVal = "";
        if (value != null)
            pRetVal = customFormat.get().format(value);
        return pRetVal;
    }
}
