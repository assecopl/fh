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

@FhFormatter("bigDecimalFormatter6")
public class BigDecimalFormatter6 extends AutoRegisteredFormatter<BigDecimal> {
    private static final int PRECISION = 6;

    private static ThreadLocal<DecimalFormat> customFormat = ThreadLocal.withInitial(() -> new DecimalFormat() {{
        setGroupingUsed(false);
        setParseBigDecimal(true);
        DecimalFormatSymbols pSymbols = getDecimalFormatSymbols();
        pSymbols.setDecimalSeparator(',');
        pSymbols.setGroupingSeparator(' ');
        setDecimalFormatSymbols(pSymbols);
        setMinimumFractionDigits(0);
        setMaximumFractionDigits(PRECISION);
        setRoundingMode(RoundingMode.DOWN);
    }});

    @Override
    public BigDecimal parse(String s, Locale locale) throws NumberFormatException {
        return new BigDecimal(s).setScale(PRECISION, RoundingMode.DOWN);
    }

    @Override
    public String print(BigDecimal value, Locale locale) {
        String pRetVal = "";
        if (value != null)
            pRetVal = customFormat.get().format(value);
        return pRetVal;
    }
}
