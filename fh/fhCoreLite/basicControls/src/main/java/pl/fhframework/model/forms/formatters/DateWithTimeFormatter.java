package pl.fhframework.model.forms.formatters;

import org.springframework.format.datetime.DateFormatter;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@FhFormatter("dateWithTimeFormatter")
public class DateWithTimeFormatter extends DateFormatter {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Date parse(String s, Locale locale) throws ParseException {
        return new SimpleDateFormat(FORMAT).parse(s);
    }

    @Override
    public String print(Date date, Locale locale) {
        return date != null ? new SimpleDateFormat(FORMAT).format(date) : "";
    }
}
