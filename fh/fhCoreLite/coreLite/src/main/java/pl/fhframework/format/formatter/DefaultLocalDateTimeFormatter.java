package pl.fhframework.format.formatter;


import pl.fhframework.format.AutoRegisteredFormatter;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@FhFormatter
public class DefaultLocalDateTimeFormatter extends AutoRegisteredFormatter<LocalDateTime> {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(FORMAT);
        return (text == null ? null : LocalDateTime.parse(text, dtf)) ;
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(FORMAT);
        return (object == null ? null : object.format(dtf).toString());
    }

}
