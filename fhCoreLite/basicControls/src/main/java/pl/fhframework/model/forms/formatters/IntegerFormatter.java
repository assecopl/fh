package pl.fhframework.model.forms.formatters;

import org.springframework.util.StringUtils;
import pl.fhframework.format.AutoRegisteredFormatter;
import pl.fhframework.format.FhFormatter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

@FhFormatter("IntegerFormatter")
public class IntegerFormatter extends AutoRegisteredFormatter<Integer> {
    private static ThreadLocal<DecimalFormat> customFormat = ThreadLocal.withInitial(() -> new DecimalFormat() {{
        DecimalFormatSymbols pSymbols = getDecimalFormatSymbols();
        pSymbols.setDecimalSeparator(',');
        pSymbols.setGroupingSeparator(' ');
        setDecimalFormatSymbols(pSymbols);
        setGroupingUsed(false);
        setParseBigDecimal(true);
        setMinimumFractionDigits(0);
        setMaximumFractionDigits(2);
        setRoundingMode(RoundingMode.HALF_UP);
    }});

    @Override
    public Integer parse(String s, Locale locale) throws ParseException {
        if (!StringUtils.isEmpty(s)){
            String[] a = s.split("\\.");
            s = a[0];
        }
        return (Integer) Integer.parseInt(s);
    }

    @Override
    public String print(Integer value, Locale locale) {
        String pRetVal = "";
        if (value != null)
            pRetVal = value.toString();
        return pRetVal;
    }
}
