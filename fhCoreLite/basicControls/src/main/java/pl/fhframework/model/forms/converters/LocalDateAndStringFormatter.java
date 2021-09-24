package pl.fhframework.model.forms.converters;

import pl.fhframework.format.AutoRegisteredFormatter;
import pl.fhframework.format.FhFormatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

@FhFormatter
public class LocalDateAndStringFormatter extends AutoRegisteredFormatter<LocalDate> {

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return (text == null ? null : LocalDate.parse(text)) ;
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return (object == null ? null : object.toString());
    }
}
