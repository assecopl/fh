package pl.fhframework.model.forms.formatters;

import org.springframework.format.Formatter;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.util.Base64;
import java.util.Locale;

@FhFormatter("base64Formatter")
public class Base64Formatter implements Formatter<String> {
    @Override
    public String parse(String s, Locale locale) throws ParseException {
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    @Override
    public String print(String s, Locale locale) {
        return new String(Base64.getDecoder().decode(s.getBytes()));
    }
}
