package pl.fhframework.model.forms.formatters;


import pl.fhframework.format.AutoRegisteredFormatter;
import pl.fhframework.format.FhFormatter;
import java.math.BigDecimal;
import java.util.Locale;

@FhFormatter("bigDecimalFormatter")
public class BigDecimalFormatter extends AutoRegisteredFormatter<BigDecimal> {

    @Override
    public BigDecimal parse(String s, Locale locale) throws NumberFormatException {
        return new BigDecimal(s);
    }

    @Override
    public String print(BigDecimal value, Locale locale) {
        String pRetVal = "";
        if (value != null)
            pRetVal = value.toString();
        return pRetVal;
    }
}
