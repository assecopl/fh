package pl.fhframework.format.formatter;


import org.springframework.format.Formatter;
import pl.fhframework.format.AutoRegisteredFormatter;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@FhFormatter("optionalLocalDateTimeFormatter")
public class OptionalLocalDateTimeFormatter implements Formatter<LocalDateTime> {

    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(FORMAT);
        LocalDateTime l = text == null ? null : LocalDateTime.parse(text, dtf);
        return l;
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(FORMAT);
        return (object == null ? null : object.format(dtf));
    }


}
