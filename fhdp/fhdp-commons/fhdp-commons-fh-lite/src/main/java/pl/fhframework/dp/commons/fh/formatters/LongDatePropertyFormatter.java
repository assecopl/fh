package pl.fhframework.dp.commons.fh.formatters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.Formatter;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@FhFormatter("longDatePropertyFormatter")
public class LongDatePropertyFormatter implements Formatter<LocalDateTime> {

    @Value("${date.long.pattern:\"yyyy-MM-dd HH:mm:ss\"}")
    private String pattern;

    @Override
    public LocalDateTime parse(String s, Locale locale) throws ParseException {
        return null;
    }

    @Override
    public String print(LocalDateTime s, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return String.format("%s", formatter.format(s));
    }
}

